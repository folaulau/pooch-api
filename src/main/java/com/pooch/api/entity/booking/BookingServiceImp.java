package com.pooch.api.entity.booking;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCareServiceCreateDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.dto.TransactionDTO;
import com.pooch.api.entity.booking.careservice.BookingCareService;
import com.pooch.api.entity.booking.careservice.BookingCareServiceRepository;
import com.pooch.api.entity.booking.pooch.BookingPooch;
import com.pooch.api.entity.booking.pooch.BookingPoochRepository;
import com.pooch.api.entity.booking.transaction.Transaction;
import com.pooch.api.entity.booking.transaction.TransactionService;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceDAO;
import com.pooch.api.entity.notification.NotificationService;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.parent.ParentService;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodDAO;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodService;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.exception.ApiError;
import com.pooch.api.exception.ApiException;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.customer.StripeCustomerService;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.pooch.api.utils.ObjectUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentUpdateParams;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {

  @Autowired
  private BookingDAO bookingDAO;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private BookingCareServiceRepository bookingCareServiceRepository;

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private ParentDAO parentDAO;


  @Autowired
  private CareServiceDAO careServiceDAO;

  @Autowired
  private PoochDAO poochDAO;

  @Autowired
  private BookingValidatorService bookingValidatorService;

  @Autowired
  private StripePaymentIntentService stripePaymentIntentService;

  @Autowired
  private PaymentMethodService paymentMethodService;

  @Autowired
  private PaymentMethodDAO paymentMethodDAO;

  @Autowired
  private BookingPoochRepository bookingPoochRepository;

  @Autowired
  private StripeCustomerService stripeCustomerService;

  @Autowired
  private StripePaymentMethodService stripePaymentMethodService;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private NotificationService notificationService;

  private Booking findByUuid(String uuid) {
    return bookingDAO.getByUuid(uuid).orElseThrow(
        () -> new ApiException(ApiError.DEFAULT_MSG, "Booking not found for uuid=" + uuid));
  }

  @Override
  public BookingDTO book(BookingCreateDTO bookingCreateDTO) {
    Triple<Groomer, Parent, PaymentIntent> triple =
        bookingValidatorService.validateBook(bookingCreateDTO);

    Groomer groomer = triple.getLeft();
    Parent parent = triple.getMiddle();
    com.stripe.model.PaymentIntent paymentIntent = triple.getRight();

    paymentIntent = stripePaymentIntentService.capture(paymentIntent);

    Booking booking = entityDTOMapper.mapBookingCreateDTOToBooking(bookingCreateDTO);

    if (groomer.isStripeReady()) {

      com.stripe.model.Transfer transfer = stripePaymentIntentService
          .transferFundsToGroomerOnBookingInitialPayment(paymentIntent, groomer);

      if (transfer != null && transfer.getId() != null && groomer.getInstantBooking()) {
        booking.setStatus(BookingStatus.Booked);
        booking.setStripePaymentIntentTransferId(transfer.getId());
        
      } else {
        booking.setStatus(BookingStatus.Pending_Groomer_Approval);
      }

    } else {

      booking.setStatus(BookingStatus.Pending_Groomer_Approval);
    }

    booking.setStripePaymentIntentId(paymentIntent.getId());

    // log.info("paymentIntent={}", paymentIntent.toJson());

    Optional<PaymentMethod> optPaymentMethod =
        paymentMethodDAO.getByParentIdAndStripeId(parent.getId(), paymentIntent.getPaymentMethod());

    PaymentMethod paymentMethod = null;

    // log.info("stripePaymentMethod={}", stripePaymentMethod.toJson());

    if (optPaymentMethod.isPresent()) {
      paymentMethod = optPaymentMethod.get();
    } else {

      com.stripe.model.PaymentMethod stripePaymentMethod =
          stripePaymentMethodService.getById(paymentIntent.getPaymentMethod());

      if (paymentIntent.getSetupFutureUsage() != null
          && paymentIntent.getSetupFutureUsage().equalsIgnoreCase("off_session")) {
        paymentMethod = paymentMethodService.add(parent, stripePaymentMethod);
      } else {
        paymentMethod =
            paymentMethodService.mapStripePaymentMethodToPaymentMethod(stripePaymentMethod);
      }
    }

    booking.setPaymentMethod(entityDTOMapper.mapPaymentMethodToBookingPaymentMethod(paymentMethod));

    // log.info("paymentMethod={}", ObjectUtils.toJson(paymentMethod));

    parent = parentDAO.save(parent);

    booking.setParent(parent);

    booking.setGroomer(groomer);



    BookingCostDetails costDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

    booking.populateBookingCostDetails(costDetails);

    booking = bookingDAO.save(booking);

    // log.info("booking={}", ObjectUtils.toJson(booking));

    booking = addPoochesToBooking(booking, bookingCreateDTO.getPooches());

    // log.info("booking with pooches={}", ObjectUtils.toJson(booking));

    booking = bookingDAO.save(booking);

    // log.info("booking={}", ObjectUtils.toJson(booking));

    Transaction transaction = transactionService.addBookingInitialPayment(booking, costDetails);

    TransactionDTO transactionDTO = entityDTOMapper.mapTransactionToTransactionDTO(transaction);

    notificationService.sendBookingDetailsUponBooking(booking, booking.getParent(),
        booking.getGroomer());

    // 1. handle calendar
    // 2. send notification

    BookingDTO bookingDTO = entityDTOMapper.mapBookingToBookingDTO(booking);
    bookingDTO.addTransaction(transactionDTO);

    return bookingDTO;
  }

  private Booking addPoochesToBooking(Booking booking, Set<PoochBookingCreateDTO> poochCreateDTOs) {
    if (poochCreateDTOs != null) {
      int count = 0;
      for (PoochBookingCreateDTO petCreateDTO : poochCreateDTOs) {
        String uuid = petCreateDTO.getUuid();

        Pooch pooch = null;
        if (uuid == null) {
          pooch = entityDTOMapper.mapPoochBookingCreateDTOToPooch(petCreateDTO);
          pooch.setParent(booking.getParent());
          pooch = poochDAO.save(pooch);
        } else {
          pooch = poochDAO.getByUuid(uuid).get();
        }

        Set<BookingCareServiceCreateDTO> requestedCareServices =
            petCreateDTO.getRequestedCareServices();

        if (count == 0) {
          for (BookingCareServiceCreateDTO bookingCareServiceDTO : requestedCareServices) {

            CareService careService =
                careServiceDAO.getByUuid(bookingCareServiceDTO.getUuid()).get();

            BookingCareService bookingCareService =
                entityDTOMapper.mapCareServiceToBookingCareService(careService);

            bookingCareService.setBooking(booking);
            bookingCareService
                .setPrice(careService.getByPoochSize(bookingCareServiceDTO.getSize()));
            bookingCareService.setSize(bookingCareServiceDTO.getSize());

            bookingCareService = bookingCareServiceRepository.saveAndFlush(bookingCareService);

            booking.addCareService(bookingCareService);
          }
        }


        BookingPooch bookingPooch = entityDTOMapper.mapPoochToBookingPooch(pooch);
        bookingPooch.setBooking(booking);

        bookingPooch.setVaccines(bookingPooch.getVaccines().stream().map(vaccine -> {
          vaccine.setId(null);
          return vaccine;
        }).collect(Collectors.toSet()));

        // log.info("bookingPooch={}", ObjectUtils.toJson(bookingPooch));

        bookingPooch = bookingPoochRepository.saveAndFlush(bookingPooch);

        booking.addPooch(bookingPooch);

        count++;
      }

    }

    return booking;
  }

  @Override
  public BookingDTO cancel(BookingCancelDTO bookingCancelDTO) {
    Booking booking = bookingValidatorService.validateCancel(bookingCancelDTO);

    /**
     * 
     */

    com.stripe.model.PaymentIntent paymentIntent =
        stripePaymentIntentService.getById(booking.getStripePaymentIntentId());

    BookingCostDetails costDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

    Pair<Double, Double> pair =
        stripePaymentIntentService.cancelBooking(booking);

    Double amountRefunded = pair.getFirst();

    Double amountNonRefunded = pair.getSecond();

    log.info("amountRefunded={}", amountRefunded);

    booking.setCancellationRefundedAmount(amountRefunded);
    booking.setCancellationNonRefundedAmount(amountNonRefunded);

    booking.setStatus(BookingStatus.Cancelled);

    booking = bookingDAO.save(booking);

    Transaction transaction =
        transactionService.addBookingCancellation(booking, amountRefunded, amountNonRefunded);

    notificationService.sendBookingCancellation(booking, booking.getParent(), booking.getGroomer());



    return entityDTOMapper.mapBookingToBookingDTO(booking);
  }

  @Override
  public BookingDTO checkIn(String uuid) {
    Booking booking = findByUuid(uuid);
    booking.setCheckedIn(true);
    booking.setCheckedInAt(LocalDateTime.now());
    booking.setStatus(BookingStatus.Checked_In);

    return entityDTOMapper.mapBookingToBookingDTO(bookingDAO.save(booking));
  }

  @Override
  public BookingDTO checkOut(String uuid) {
    Booking booking = findByUuid(uuid);
    booking.setCheckedOut(true);
    booking.setCheckedOutAt(LocalDateTime.now());
    booking.setStatus(BookingStatus.Checked_Out);

    return entityDTOMapper.mapBookingToBookingDTO(bookingDAO.save(booking));
  }

}
