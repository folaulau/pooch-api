package com.pooch.api.entity.paymentmethod;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pooch.api.dto.EntityDTOMapper;
import com.pooch.api.dto.PaymentMethodDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fkaveinga
 *
 */
@Slf4j
@Tag(name = "Paymentmethods", description = "PaymentMethod Operations")
@RestController
@RequestMapping("/paymentmethods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @Autowired
    private EntityDTOMapper      entityMapper;

    /**
     * Add Card PaymentMethod
     * 
     * @param Customer
     * @return Boolean - true: proceed to buy, false: show error message
     * @throws ExecutionException
     * @throws InterruptedException
     */
    // @Operation(summary = "Add Card PaymentMethod")
    // @PostMapping({"/card"})
    // public ResponseEntity<List<PaymentMethodDTO>> addCardPaymentMethod(@RequestHeader(name = "token", required =
    // false) String token,
    // @Parameter(name = "accountUuid", required = true, example = "account uid") @RequestParam("accountUuid") String
    // accountUuid,
    // @Parameter(name = "Card", required = true, example = "card") @Valid @RequestBody CardPMCreateDTO cardPMCreateDTO)
    // {
    // log.debug("addCardPaymentMethod(...)");
    //
    // PaymentMethod paymentMethod = entityMapper.mapCardPMCreateDTOToPaymentMethod(cardPMCreateDTO);
    //
    // paymentMethod.setType(PaymentMethodType.CARD);
    //
    // List<PaymentMethod> paymentMethods = paymentMethodService.add(accountUuid, paymentMethod);
    //
    // return new ResponseEntity<>(entityMapper.mapPaymentMethodsToPaymentMethodDTOs(paymentMethods), HttpStatus.OK);
    // }

    // @Operation(summary = "Update Card PaymentMethod")
    // @PutMapping({"/card"})
    // public ResponseEntity<List<PaymentMethodDTO>> updateCardPaymentMethod(@Parameter(name = "Card", required = true,
    // example = "card") @Valid @RequestBody CardPMCreateDTO cardPMCreateDTO,
    // @Parameter(name = "uuid", required = true, example = "payment method uuid") @RequestParam("uuid") String uuid) {
    // log.debug("updateCardPaymentMethod(...)");
    //
    // PaymentMethod newPaymentMethod = entityMapper.mapCardPMCreateDTOToPaymentMethod(cardPMCreateDTO);
    //
    // List<PaymentMethod> paymentMethods = paymentMethodService.update(ApiSessionUtils.getAccountId(), uuid,
    // newPaymentMethod);
    //
    // return new ResponseEntity<>(entityMapper.mapPaymentMethodsToPaymentMethodDTOs(paymentMethods), HttpStatus.OK);
    //
    // }
    //
    // @Operation(summary = "Delete PaymentMethod")
    // @DeleteMapping
    // public ResponseEntity<List<PaymentMethodDTO>> deletePaymentMethod(@RequestHeader(name = "token", required =
    // false) String token,
    // @Parameter(name = "customerUid", required = true, example = "customer uid") @RequestParam("customerUid") String
    // customerUid,
    // @Parameter(name = "paymentMethod", required = true, example = "Payment Method") @Valid @RequestBody
    // PaymentMethodDeleteDTO paymentMethodDeleteDTO) {
    // log.debug("deletePaymentMethod(...)");
    //
    // List<PaymentMethod> paymentMethods = paymentMethodService.remove(ApiSessionUtils.getAccountId(),
    // paymentMethodDeleteDTO);
    //
    // return new ResponseEntity<>(entityMapper.mapPaymentMethodsToPaymentMethodDTOs(paymentMethods), HttpStatus.OK);
    // }
}
