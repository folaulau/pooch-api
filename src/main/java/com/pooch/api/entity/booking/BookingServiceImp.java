package com.pooch.api.entity.booking;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.ParentCreateUpdateDTO;
import com.pooch.api.dto.BookingCancelDTO;
import com.pooch.api.dto.BookingCreateDTO;
import com.pooch.api.dto.BookingDTO;
import com.pooch.api.dto.PoochCreateDTO;
import com.pooch.api.dto.PoochDTO;
import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerDAO;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentDAO;
import com.pooch.api.entity.parent.ParentService;
import com.pooch.api.entity.paymentmethod.PaymentMethod;
import com.pooch.api.entity.paymentmethod.PaymentMethodDAO;
import com.pooch.api.entity.paymentmethod.PaymentMethodService;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochDAO;
import com.pooch.api.library.stripe.paymentintent.StripePaymentIntentService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.pooch.api.utils.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImp implements BookingService {

    @Autowired
    private BookingDAO                 bookingDAO;

    @Autowired
    private EntityDTOMapper            entityDTOMapper;

    @Autowired
    private GroomerDAO                 groomerDAO;

    @Autowired
    private ParentDAO                  parentDAO;

    @Autowired
    private PoochDAO                   poochDAO;

    @Autowired
    private BookingValidatorService    bookingValidatorService;

    @Autowired
    private StripePaymentIntentService stripePaymentIntentService;

    @Autowired
    private PaymentMethodService       paymentMethodService;

    @Autowired
    private PaymentMethodDAO           paymentMethodDAO;

    @Autowired
    private ParentService              parentService;

    @Autowired
    private StripePaymentMethodService stripePaymentMethodService;

    @Override
    public BookingDTO bookAsQuest(BookingCreateDTO bookingCreateDTO) {
        bookingValidatorService.validateBook(bookingCreateDTO);

        Booking booking = entityDTOMapper.mapBookingCreateDTOToBooking(bookingCreateDTO);

        ParentCreateUpdateDTO parentCreateUpdateDTO = bookingCreateDTO.getParent();

        Parent parent = null;

        if (parentCreateUpdateDTO.getUuid() != null) {
            parent = parentDAO.getByUuid(parentCreateUpdateDTO.getUuid()).get();
            entityDTOMapper.patchParentWithNewParentUpdateDTO(parentCreateUpdateDTO, parent);
        } else {
            parent = entityDTOMapper.mapNewUpdateDTOToParent(parentCreateUpdateDTO);
        }

        com.stripe.model.PaymentIntent paymentIntent = stripePaymentIntentService.getById(bookingCreateDTO.getPaymentIntentId());

        log.info("paymentIntent={}", paymentIntent.toJson());
        parent.setStripeCustomerId(paymentIntent.getCustomer());

        Optional<PaymentMethod> optPaymentMethod = paymentMethodDAO.getByParentIdAndStripeId(parent.getId(), paymentIntent.getPaymentMethod());

        com.stripe.model.PaymentMethod stripePaymentMethod = stripePaymentMethodService.getById(paymentIntent.getPaymentMethod());

        PaymentMethod paymentMethod = null;

        // log.info("stripePaymentMethod={}", stripePaymentMethod.toJson());

        if (optPaymentMethod.isPresent()) {
            paymentMethod = optPaymentMethod.get();
        } else {
            paymentMethod = paymentMethodService.add(parent, stripePaymentMethod);
        }

        booking.setPaymentMethod(paymentMethod);

        // log.info("paymentMethod={}", ObjectUtils.toJson(paymentMethod));

        parent = parentDAO.save(parent);

        booking.setParent(parent);

        Groomer groomer = groomerDAO.getByUuid(bookingCreateDTO.getGroomerUuid()).get();

        booking.setGroomer(groomer);

        booking = addPoochesToBooking(booking, bookingCreateDTO.getPooches());

        log.info("booking={}", ObjectUtils.toJson(booking));
        
        booking.setRequestAsJson(ObjectUtils.toJson(bookingCreateDTO));

        booking = bookingDAO.save(booking);

        return entityDTOMapper.mapBookingToBookingDTO(booking);
    }

    private Booking addPoochesToBooking(Booking booking, Set<PoochCreateDTO> poochCreateDTOs) {
        if (poochCreateDTOs != null) {

            for (PoochCreateDTO petCreateDTO : poochCreateDTOs) {
                String uuid = petCreateDTO.getUuid();

                Pooch pooch = null;
                if (uuid == null) {
                    pooch = entityDTOMapper.mapPoochCreateDTOToPooch(petCreateDTO);
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
