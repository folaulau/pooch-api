package com.pooch.api.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.booking.BookingCostDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.pooch.api.entity.groomer.Groomer;
import com.pooch.api.entity.groomer.GroomerRepository;
import com.pooch.api.entity.groomer.GroomerSignUpStatus;
import com.pooch.api.entity.groomer.GroomerStatus;
import com.pooch.api.entity.groomer.careservice.CareService;
import com.pooch.api.entity.groomer.careservice.CareServiceRepository;
import com.pooch.api.entity.parent.Parent;
import com.pooch.api.entity.parent.ParentRepository;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethod;
import com.pooch.api.entity.parent.paymentmethod.PaymentMethodService;
import com.pooch.api.entity.pooch.FoodSchedule;
import com.pooch.api.entity.pooch.Gender;
import com.pooch.api.entity.pooch.Pooch;
import com.pooch.api.entity.pooch.PoochRepository;
import com.pooch.api.entity.pooch.Training;
import com.pooch.api.entity.pooch.vaccine.Vaccine;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.entity.role.Role;
import com.pooch.api.library.aws.secretsmanager.StripeSecrets;
import com.pooch.api.library.firebase.FirebaseAuthResponse;
import com.pooch.api.library.firebase.FirebaseRestClient;
import com.pooch.api.library.stripe.StripeMetadataService;
import com.pooch.api.library.stripe.paymentmethod.StripePaymentMethodService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.SetupIntentUpdateParams;
import lombok.extern.slf4j.Slf4j;

/**
 * Only for testing
 */
@Repository
@Slf4j
public class TestEntityGeneratorService {

  @Autowired
  private GroomerRepository petSitterRepository;

  @Autowired
  private ParentRepository petParentRepository;

  @Autowired
  private PoochRepository petRepository;

  @Autowired
  private CareServiceRepository careServiceRepository;

  @Autowired
  private StripePaymentMethodService stripePaymentMethodService;

  @Autowired
  private PaymentMethodService paymentMethodService;

  @Autowired
  @Qualifier(value = "stripeSecrets")
  private StripeSecrets stripeSecrets;

  @Value("${booking.fee:10}")
  private Double bookingFee;

  @Autowired
  private StripeTokenService stripeTokenService;

  @Value("${spring.profiles.active}")
  private String env;
  
  @Autowired
  private FirebaseRestClient     firebaseRestClient;

  public Groomer getDBGroomer() {
    Groomer petSitter = getGroomer();
    return petSitterRepository.saveAndFlush(petSitter);
  }
  
  public Groomer getDBFirebaseGroomer() {
    Groomer groomer = getGroomer();
    FirebaseAuthResponse firebaseAuthResponse =  firebaseRestClient.signUp(groomer.getEmail(), "Test1234!");
    groomer.setUuid(firebaseAuthResponse.getLocalId());
    groomer =  petSitterRepository.saveAndFlush(groomer);
    return groomer;
  }

  public Groomer getActiveDBGroomer() {
    Groomer petSitter = getActiveGroomer();
    return petSitterRepository.saveAndFlush(petSitter);
  }

  public Groomer getStripeReadyDBGroomer() {
    Groomer groomer = getActiveGroomer();
    groomer.setStripePayoutsEnabled(true);
    groomer.setStripeChargesEnabled(true);
    groomer.setStripeDetailsSubmitted(true);

    return petSitterRepository.saveAndFlush(groomer);
  }

  public Groomer getGroomer() {

    Groomer groomer = new Groomer();
    groomer.setUuid("groomer-" + UUID.randomUUID().toString());
    String firstName = RandomGeneratorUtils.getRandomFirstname();
    groomer.setFirstName(firstName);
    String lastName = RandomGeneratorUtils.getRandomLastname();
    groomer.setLastName(lastName);
    groomer.setEmail(RandomGeneratorUtils.getRandomEmail());
    groomer.setEmailVerified(false);
    groomer.setBusinessName((firstName + " " + lastName).toLowerCase());
    groomer.setNumberOfOccupancy(RandomGeneratorUtils.getLongWithin(2L, 100L));
    groomer.setChargePerMile(
        MathUtils.getTwoDecimalPlaces(RandomGeneratorUtils.getDoubleWithin(2D, 20D)));
    groomer.setOfferedDropOff(true);
    groomer.setOfferedPickUp(true);
    groomer.setStatus(GroomerStatus.SIGNING_UP);
    groomer.setSignUpStatus(GroomerSignUpStatus.LISTING_CREATED);
    groomer.setDescription("Test description");

    groomer.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
    groomer.setPhoneNumberVerified(false);

    groomer.setRating(MathUtils.getTwoDecimalPlaces(RandomGeneratorUtils.getDoubleWithin(1, 5)));
    groomer.addRole(new Role(UserType.groomer));

    // +6 for UTC
    int openHour = RandomGeneratorUtils.getIntegerWithin(7, 9) + 6;
    LocalTime openTime =
        LocalTime.of(openHour, 0).withNano(0).withSecond(0);
    groomer.setOpenTime(openTime);
    
    groomer.setCloseTime(openTime.plusHours(8));

    groomer.setAddress(getAddress());
    return groomer;
  }

  public Groomer getActiveGroomer() {

    Groomer groomer = new Groomer();
    groomer.setUuid("groomer-" + UUID.randomUUID().toString());
    String firstName = RandomGeneratorUtils.getRandomFirstname();
    groomer.setFirstName(firstName);
    String lastName = RandomGeneratorUtils.getRandomLastname();
    groomer.setLastName(lastName);
    groomer.setEmail(RandomGeneratorUtils.getRandomEmail());
    groomer.setEmailVerified(false);
    groomer.setBusinessName((firstName + " " + lastName).toLowerCase());
    groomer.setNumberOfOccupancy(RandomGeneratorUtils.getLongWithin(2L, 20L));
    groomer.setChargePerMile(RandomGeneratorUtils.getDoubleWithin(1D, 3D));
    groomer.setOfferedDropOff(true);
    groomer.setOfferedPickUp(true);
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer.setSignUpStatus(GroomerSignUpStatus.COMPLETED);
    groomer.setDescription("Test description");

    groomer.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
    groomer.setPhoneNumberVerified(false);

    groomer.setRating(RandomGeneratorUtils.getDoubleWithin(1, 5));
    groomer.addRole(new Role(UserType.groomer));

    // +6 for UTC
    int openHour = RandomGeneratorUtils.getIntegerWithin(7, 9) + 6;
    LocalTime openTime =
        LocalTime.of(openHour, 0).withNano(0).withSecond(0);
    LocalTime closeTimme = openTime.plusHours(8);

    groomer.setOpenTime(openTime);
    groomer.setCloseTime(closeTimme);

    groomer.setAddress(getAddress());

    return groomer;
  }

  public Parent getDBParent() {
    Parent petParent = getParent();
    return petParentRepository.saveAndFlush(petParent);
  }

  public Parent getDBParentWithStripeCustomer() {
    Parent petParent = getParent();
    com.stripe.model.Customer customer = createCustomer(petParent, false);
    petParent.setStripeCustomerId(customer.getId());

    return petParentRepository.saveAndFlush(petParent);
  }

  public CareService getDBCareService(Groomer groomer) {
    CareService careService = getCareService(groomer);
    return careServiceRepository.saveAndFlush(careService);
  }

  public CareService getCareService() {
    return getCareService(null);
  }

  public CareService getCareService(Groomer groomer) {
    List<String> serviceTypes =
        Arrays.asList("Dog Daycare", "Grooming", "Overnight", "Pick up/Drop off", "Nail Clipping");
    CareService careService = CareService.builder().serviceSmall(true)
        .smallPrice(RandomGeneratorUtils.getDoubleWithin(10, 30)).serviceMedium(true)
        .mediumPrice(RandomGeneratorUtils.getDoubleWithin(30, 50)).serviceLarge(true)
        .largePrice(RandomGeneratorUtils.getDoubleWithin(50, 70))
        .name(serviceTypes.get(RandomGeneratorUtils.getIntegerWithin(0, 4))).groomer(groomer)
        .build();

    return careService;
  }

  public Parent getParent() {

    Parent petParent = new Parent();
    petParent.setUuid("parent-" + UUID.randomUUID().toString());
    String firstName = RandomGeneratorUtils.getRandomFirstname();
    String lastName = RandomGeneratorUtils.getRandomLastname();
    petParent.setFullName(firstName + " " + lastName);
    petParent.setEmail(RandomGeneratorUtils.getRandomEmail());
    petParent.setEmailVerified(false);
    petParent.setPhoneNumber(RandomGeneratorUtils.getLongWithin(3101000000L, 3109999999L));
    petParent.setPhoneNumberVerified(false);
    petParent.addRole(new Role(UserType.parent));

    petParent.setAddress(getAddress());

    return petParent;
  }

  public Pooch getDBPooch(Parent petParent) {
    Pooch pet = getPooch(petParent);
    return petRepository.saveAndFlush(pet);
  }

  public Pooch getPooch(Parent petParent) {

    Pooch pooch = new Pooch();
    pooch.setBreed("Bulldog");
    pooch.setParent(petParent);
    pooch.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(6, 60)));
    pooch.addFoodSchedule(FoodSchedule.Morning);
    pooch.addFoodSchedule(FoodSchedule.Night);

    pooch.setFullName(RandomGeneratorUtils.getRandomFullName());
    pooch.setGender(Gender.Female);
    pooch.setSpayed(true);
    pooch.setTraining(Training.Medium);
    pooch.setWeight(15D);
    pooch.addVaccine(
        Vaccine.builder().name("vitamin").expireDate(LocalDateTime.now().plusMonths(6)).build());
    pooch.addVaccine(
        Vaccine.builder().name("protein").expireDate(LocalDateTime.now().plusMonths(6)).build());


    return pooch;
  }

  public Address getAddress() {

    List<Address> addresses = new ArrayList<>();

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3113 Pico Blvd");
    address.setLatitude(34.026150);
    address.setLongitude(-118.457170);

    addresses.add(address);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90404");
    address.setStreet("1222 26th St");
    address.setLatitude(34.035290);
    address.setLongitude(-118.477080);

    addresses.add(address);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("1502 Maple St");
    address.setLatitude(34.011520);
    address.setLongitude(-118.469900);

    addresses.add(address);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90402");
    address.setStreet("835 San Vicente Blvd");
    address.setLatitude(34.034222);
    address.setLongitude(-118.507141);

    addresses.add(address);

    /** berverly hills */
    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90024");
    address.setStreet("719 S Beverly Glen Blvd");
    address.setLatitude(34.0698483);
    address.setLongitude(-118.4310435);

    addresses.add(address);

    address = new Address();
    address.setCity("Beverly Hills");
    address.setState("CA");
    address.setZipcode("90210");
    address.setStreet("620 Walden Dr");
    address.setLatitude(34.0703334);
    address.setLongitude(-118.4137745);

    addresses.add(address);

    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90025");
    address.setStreet("2062 Kerwood Ave");
    address.setLatitude(34.0535801);
    address.setLongitude(-118.41946);

    addresses.add(address);

    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90064");
    address.setStreet("2320 Manning Ave");
    address.setLatitude(34.0444587);
    address.setLongitude(-118.4239185);

    addresses.add(address);

    /** Los Angeles */
    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90019");
    address.setStreet("1129 Queen Anne Pl");
    address.setLatitude(34.0534423);
    address.setLongitude(-118.3308591);

    addresses.add(address);

    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90004");
    address.setStreet("237 N Berendo St");
    address.setLatitude(34.0755959);
    address.setLongitude(-118.294399);

    addresses.add(address);

    address = new Address();
    address.setCity("Los Angeles");
    address.setState("CA");
    address.setZipcode("90031");
    address.setStreet("3862 N Broadway");
    address.setLatitude(34.0737122);
    address.setLongitude(-118.197979);

    addresses.add(address);

    /**
     * 1800 Ocean Front Walk, Venice, CA 90291
     * 
     * 
     * 
     * 
     * 
     * 10:53 THIS IS THE LAT 33.9850469 this is the long -118.4694832
     */

    address = new Address();
    address.setCity("Venice");
    address.setState("CA");
    address.setZipcode("90291");
    address.setStreet("1800 Ocean Front Walk");
    address.setLatitude(33.9850469);
    address.setLongitude(-118.4694832);

    addresses.add(address);

    return addresses.get(RandomGeneratorUtils.getIntegerWithin(0, addresses.size() - 1));
  }

  public Pooch getPooch() {
    Pooch pooch = new Pooch();
    pooch.setBreed("Bulldog");
    pooch.setDob(LocalDate.now().minusMonths(RandomGeneratorUtils.getLongWithin(1, 12)));
    pooch.addFoodSchedule(FoodSchedule.Morning);
    pooch.addFoodSchedule(FoodSchedule.Night);
    pooch.setFullName(RandomGeneratorUtils.getRandomFullName());
    pooch.setGender(Gender.Male);
    pooch.setNeutered(true);
    pooch.setTraining(Training.Medium);
    pooch.setWeight(RandomGeneratorUtils.getDoubleWithin(1.0, 30.0));
    return pooch;
  }

  public PaymentIntent createAndAddPaymentMethod(Double amount, String paymentMethodId) {
    return createAndAddPaymentMethod(amount, paymentMethodId, null);
  }

  public PaymentIntent createAndAddPaymentMethod(Double amount, String paymentMethodId,
      String customerId) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    List<String> paymentMethodTypes = new ArrayList<>();
    paymentMethodTypes.add("card");

    // $10 booking fee
    double bookingCost = amount;

    double chargeAmount = bookingCost + bookingFee;
    // 2.9% of chargeAmount + 30 cents
    double stripeFee = BigDecimal.valueOf(2.9).divide(BigDecimal.valueOf(100))
        .multiply(BigDecimal.valueOf(chargeAmount)).add(BigDecimal.valueOf(0.3))
        .setScale(2, RoundingMode.HALF_EVEN).doubleValue();

    double totalCharge = chargeAmount + stripeFee;

    long totalChargeAsCents =
        BigDecimal.valueOf(totalCharge).multiply(BigDecimal.valueOf(100)).longValue();

    // String customerId = "cus_Lgyk8DhX8TytPQ";

    // @formatter:off
        
        BookingCostDetails costDetails = BookingCostDetails.builder()
                .careServicesCost(bookingCost)
                .bookingFee(bookingFee)
                .stripeFee(stripeFee)
                .build();
        
        PaymentIntentCreateParams.Builder builder =
        PaymentIntentCreateParams.builder()
                .addPaymentMethodType("card")
                .setAmount(totalChargeAsCents)
                .setCurrency("usd")
                .setSetupFutureUsage(PaymentIntentCreateParams.SetupFutureUsage.OFF_SESSION)
                .putMetadata(StripeMetadataService.env, env)
                .putMetadata(StripeMetadataService.PAYMENTINTENT_BOOKING_DETAILS, costDetails.toJson())
//                .setConfirm(true)
                .setTransferGroup("group-" + UUID.randomUUID().toString());
        // @formatter:on

    if (customerId != null) {
      builder.setCustomer(customerId);
    }

    if (paymentMethodId != null) {
      builder.setPaymentMethod(paymentMethodId);
    }

    PaymentIntentCreateParams createParams = builder.build();

    PaymentIntent paymentIntent = null;

    try {
      paymentIntent = PaymentIntent.create(createParams);
      log.info("paymentIntent={}", paymentIntent.toJson());
    } catch (StripeException e) {
      log.warn("StripeException, msg={}", e.getMessage());
    }

    return paymentIntent;
  }

  public com.stripe.model.Customer createCustomer() {
    return createCustomer(null);
  }

  public com.stripe.model.Customer createCustomer(Parent parent) {
    return createCustomer(parent, false);
  }

  public com.stripe.model.Customer createCustomer(Parent parent, boolean addPaymentMethod) {
    Stripe.apiKey = stripeSecrets.getSecretKey();

    //@formatter:off
        
        CustomerCreateParams.Builder builder = CustomerCreateParams.builder()
                .setMetadata(Map.of(StripeMetadataService.env,env))
                .setName(parent.getFullName());
        
        if(parent!=null) {
            builder.setName(parent.getFullName()); 
            builder.setEmail(parent.getEmail());
        }
        
        CustomerCreateParams customerParams = builder.build();
        //@formatter:on

    Customer customer = null;

    try {
      customer = Customer.create(customerParams);
      log.info("customer={}", customer.toJson());
    } catch (Exception e) {
      log.warn("StripeException, customer, msg={}", e.getMessage());
    }

    if (addPaymentMethod) {
      addPaymentMethodToCustomer(customer);
    }

    return customer;
  }

  public String addPaymentMethodToCustomer(String customerId) {
    com.stripe.model.Customer customer = null;
    try {
      customer = Customer.retrieve(customerId);
    } catch (Exception e) {
      log.warn("StripeException, customer, msg={}", e.getMessage());
    }
    return addPaymentMethodToCustomer(customer);
  }

  public String addPaymentMethodToCustomer(com.stripe.model.Customer customer) {

    com.stripe.model.PaymentMethod paymentMethod = stripeTokenService.getCardPaymentMethod();

    stripePaymentMethodService.attachToCustomer(paymentMethod.getId(), customer.getId());

    return paymentMethod.getId();
  }

  public String getPaymentMethod(String name) {
    com.stripe.model.PaymentMethod paymentMethod = stripeTokenService.getCardPaymentMethod();

    log.info("paymentMethod={}", paymentMethod.toJson());
    return paymentMethod.getId();
  }

  public void addPaymentMethod(String customerId, String paymentMethodId) {

    stripePaymentMethodService.attachToCustomer(paymentMethodId, customerId);
  }

  public PaymentMethod addPaymentMethod(Parent parent) {

    com.stripe.model.PaymentMethod paymentMethod = stripeTokenService.getCardPaymentMethod();

    PaymentMethod pm = this.paymentMethodService.add(parent, paymentMethod);

    return pm;
  }

  public SetupIntent addPaymentMethodAndConfirmSetupIntent(String setupIntentId,
      String paymentMethodId) {

    Stripe.apiKey = stripeSecrets.getSecretKey();

    SetupIntent setupIntent = null;
    try {
      setupIntent = SetupIntent.retrieve(setupIntentId);

      log.info("setupIntent={}", setupIntent.toJson());

      setupIntent = setupIntent
          .update(SetupIntentUpdateParams.builder().setPaymentMethod(paymentMethodId).build());

      log.info("updated setupIntent={}", setupIntent.toJson());

      setupIntent = setupIntent.confirm();

      log.info("confirmed setupIntent={}", setupIntent.toJson());

    } catch (StripeException e) {
      log.warn("StripeException - create, localMessage={}, userMessage={}", e.getLocalizedMessage(),
          e.getUserMessage());
    }

    return setupIntent;
  }
}
