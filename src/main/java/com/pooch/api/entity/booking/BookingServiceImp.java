package com.pooch.api.entity.booking;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.PoochBookingCreateDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.dto.PoochCreateUpdateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.booking.transaction.TransactionService;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.parent.ParentService;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodDAO;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodService;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.customer.StripeCustomerService;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {

  @Autowired
  private BookingDAO bookingDAO;

  @Autowired
  private EntityDTOMapper entityDTOMapper;

  @Autowired
  private GroomerDAO groomerDAO;

  @Autowired
  private ParentDAO parentDAO;

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
  private StripeCustomerService stripeCustomerService;

  @Autowired
  private StripePaymentMethodService stripePaymentMethodService;

  @Autowired
  private TransactionService transactionService;

  @Override
  public BookingDTO book(BookingCreateDTO bookingCreateDTO) {
    bookingValidatorService.validateBook(bookingCreateDTO);

    Booking booking = entityDTOMapper.mapBookingCreateDTOToBooking(bookingCreateDTO);

    Parent parent = parentDAO.getByUuid(bookingCreateDTO.getParentUuid()).get();

    booking.setStripePaymentIntentId(bookingCreateDTO.getPaymentIntentId());
    
    com.stripe.model.PaymentIntent paymentIntent =
        stripePaymentIntentService.getById(bookingCreateDTO.getPaymentIntentId());

    log.info("paymentIntent={}", paymentIntent.toJson());

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

    Groomer groomer = groomerDAO.getByUuid(bookingCreateDTO.getGroomerUuid()).get();

    booking.setGroomer(groomer);

    if (groomer.isStripeReady()) {

      boolean transferred =
          stripePaymentIntentService.transferFundsToGroomer(paymentIntent, groomer);

      if (transferred) {
        booking.setStatus(BookingStatus.Booked);
      } else {
        booking.setStatus(BookingStatus.Pending_Groomer_Approval);
      }

    } else {

      booking.setStatus(BookingStatus.Pending_Groomer_Approval);
    }

    booking = addPoochesToBooking(booking, bookingCreateDTO.getPooches());

    log.info("booking={}", ObjectUtils.toJson(booking));


    BookingCostDetails costDetails = BookingCostDetails.fromJson(
        paymentIntent.getMetadata().get(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS));

    booking.populateBookingCostDetails(costDetails);

    booking = bookingDAO.save(booking);

    transactionService.addBookingInitialPayment(booking, costDetails);

    // 1. handle calendar
    // 2. send notification

    return entityDTOMapper.mapBookingToBookingDTO(booking);
  }

  private Booking addPoochesToBooking(Booking booking, Set<PoochBookingCreateDTO> poochCreateDTOs) {
    if (poochCreateDTOs != null) {

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

        booking.addPooch(pooch);

      }

    }

    return booking;
  }

  @Override
  public BookingDTO cancel(BookingCancelDTO bookingCancelDTO) {
    Booking booking = bookingValidatorService.validateCancel(bookingCancelDTO);

    return null;
  }

}
