package com.pooch.api.entity.groomer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooch.api.IntegrationTestConfiguration;
import com.pooch.api.dto.*;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.elastic.repo.GroomerESRepository;
import com.pooch.api.entity.address.Address;
import com.pooch.api.entity.role.UserType;
import com.pooch.api.security.jwt.JwtPayload;
import com.pooch.api.security.jwt.JwtTokenService;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.RandomGeneratorUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureMockMvc
public class GroomerSearchIntegrationTests extends IntegrationTestConfiguration {

  @Autowired
  private MockMvc mockMvc;

  @Resource
  private WebApplicationContext webApplicationContext;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Filter springSecurityFilterChain;

  @MockBean
  private JwtTokenService jwtTokenService;

  @Autowired
  private GroomerESRepository groomerESRepository;

  @Autowired
  private GroomerService groomerService;

  @Captor
  private ArgumentCaptor<String> tokenCaptor;

  private String GROOMER_TOKEN = "GROOMER_TOKEN";
  private String GROOMER_UUID = "GROOMER_UUID";

  @Autowired
  private TestEntityGeneratorService testEntityGeneratorService;
  @Autowired
  private EntityDTOMapper entityDTOMapper;

  private List<GroomerES> groomers = null;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .addFilters(springSecurityFilterChain).build();

    JwtPayload groomerJwtPayload = new JwtPayload();
    groomerJwtPayload.setUuid(GROOMER_UUID);
    groomerJwtPayload.setRole(UserType.groomer.name());

    Mockito.when(jwtTokenService.getPayloadByToken(GROOMER_TOKEN)).thenReturn(groomerJwtPayload);

    groomers = new ArrayList<>();
  }

  // @Disabled
  @Transactional
  @Test
  void itShouldSearchWithFiltersRadius_valid() throws Exception {

    /**
     * Groomer #1<br>
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     */
   Groomer gm = testEntityGeneratorService.getGroomer();
   gm.setListing(true);
   
   GroomerES groomer =
        entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90403");
    address.setStreet("1043 Franklin St");
    address.setLatitude(34.043148);
    address.setLongitude(-118.4750169);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);
    /**
     * Groomer #2<br>
     * 1116 Stanford St, Santa Monica, CA 90403<br>
     * lat: 34.0400821, -118.475029<br>
     */
    gm = testEntityGeneratorService.getGroomer();
    gm.setListing(true);
    groomer = entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90403");
    address.setStreet("1116 Stanford St");
    address.setLatitude(34.0400821);
    address.setLongitude(-118.475029);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);

    Thread.sleep(500);

    System.out.println("groomer: " + ObjectUtils.toJson(groomer));

    double distanceFromMainGroomer =
        MathUtils.distance(34.043148, 34.0400821, -118.4750169, -118.475029);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * Groomer #3<br>
     * 3408 Pearl St, Santa Monica, CA 90405<br>
     * lat: 34.0251161, -118.4517642<br>
     */
    gm = testEntityGeneratorService.getGroomer();
    gm.setListing(true);
    groomer = entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3408 Pearl St");
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);

    Thread.sleep(1000);

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * Use groomer #1 as starting point, lat: 34.043148, long: -118.4750169<br>
     */
    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(34.043148);
    filters.setLongitude(-118.4750169);
    filters.setDistance(1);

    CustomPage<GroomerES> searchResult = groomerService.search(filters);

    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();
    // groomer 1 and 2
    assertThat(searchResult.getTotalElements()).isGreaterThanOrEqualTo(2);
  }

  @Transactional
  @Test
  void itShouldSearchWithFiltersRadiusAndDistance_valid() throws Exception {

    /**
     * Groomer #1<br>
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     */
    Groomer gm = testEntityGeneratorService.getGroomer();
    gm.setListing(true);
    GroomerES groomer =
        entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90403");
    address.setStreet("1043 Franklin St");
    address.setLatitude(34.043148);
    address.setLongitude(-118.4750169);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);
    /**
     * Groomer #2<br>
     * 1116 Stanford St, Santa Monica, CA 90403<br>
     * lat: 34.0400821, -118.475029<br>
     */
     gm = testEntityGeneratorService.getGroomer();
    gm.setListing(true);
    groomer = entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90403");
    address.setStreet("1116 Stanford St");
    address.setLatitude(34.0400821);
    address.setLongitude(-118.475029);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);

    double distanceFromMainGroomer =
        MathUtils.distance(34.043148, 34.0400821, -118.4750169, -118.475029);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * Groomer #3<br>
     * 3408 Pearl St, Santa Monica, CA 90405<br>
     * lat: 34.0251161, -118.4517642<br>
     */
     gm = testEntityGeneratorService.getGroomer();
    gm.setListing(true);
    groomer = entityDTOMapper.mapGroomerEntityToGroomerES(gm);

    address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3408 Pearl St");
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);

    Thread.sleep(1000);

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * Use groomer #1 as starting point, lat: 34.043148, long: -118.4750169<br>
     */
    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(34.043148);
    filters.setLongitude(-118.4750169);
    filters.setDistance(1);
    filters.addSort(GroomerSearchSorting.rating.name());
    filters.addSort(GroomerSearchSorting.distance.name());

    CustomPage<GroomerES> searchResult = groomerService.search(filters);

    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();
    // groomer 1 and 2
    assertThat(searchResult.getTotalElements()).isGreaterThanOrEqualTo(2);
  }


  @Transactional
  @Test
  void itShouldSearch_dropoff_pickup_costs() throws Exception {

    /**
     * Groomer #1<br>
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     */
    Groomer groomerDB = testEntityGeneratorService.getGroomer();
    groomerDB.setListing(true);
    System.out.println("name: " + groomerDB.getFullName());

    double chargePerMile = 5D;

    groomerDB.setChargePerMile(chargePerMile);

    GroomerES groomer = entityDTOMapper.mapGroomerEntityToGroomerES(groomerDB);

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3408 Pearl St");
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);


    /**
     * Use groomer #1 as starting point, lat: 34.043148, long: -118.4750169<br>
     */
    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(34.043148);
    filters.setLongitude(-118.4750169);
    filters.setDistance(5);

    CustomPage<GroomerES> searchResult = groomerService.search(filters);

    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();
    // groomer 1 and 2
    assertThat(searchResult.getTotalElements()).isGreaterThanOrEqualTo(1);

    Optional<GroomerES> optGroomer = searchResult.getContent().stream()
        .filter(grmrEs -> groomerDB.getUuid().equalsIgnoreCase(grmrEs.getUuid())).findFirst();
    
    assertThat(optGroomer.isPresent()).isTrue();
    
    GroomerES gromer = optGroomer.get();
   
    assertThat(gromer.getChargePerMile()).isEqualTo(chargePerMile);
    
    // distanceFromSearch 1.82
    
    assertThat(gromer.getDropOffCost()).isEqualTo(9.1);
    assertThat(gromer.getPickUpCost()).isEqualTo(9.1);

  }
  
  /**
   * Groomer turned off pickup and dropoff<br>
   * pickupCost = 0<br>
   * dropoffCost = 0<br>
   */
  @Transactional
  @Test
  void itShouldSearch_with_disabled_dropoff_pickup() throws Exception {

    /**
     * Groomer #1<br>
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     */
    Groomer groomerDB = testEntityGeneratorService.getGroomer();
    groomerDB.setListing(true);
    System.out.println("name: " + groomerDB.getFullName());

    double chargePerMile = 5D;

    groomerDB.setChargePerMile(chargePerMile);
    groomerDB.setOfferedDropOff(false);
    groomerDB.setOfferedPickUp(false);
    
    GroomerES groomer = entityDTOMapper.mapGroomerEntityToGroomerES(groomerDB);

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3408 Pearl St");
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    groomer.setId(RandomGeneratorUtils.getLongWithin(1000000, 100000000));
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);


    /**
     * Use groomer #1 as starting point, lat: 34.043148, long: -118.4750169<br>
     */
    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(34.043148);
    filters.setLongitude(-118.4750169);
    filters.setDistance(5);

    CustomPage<GroomerES> searchResult = groomerService.search(filters);

    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();
    // groomer 1 and 2
    assertThat(searchResult.getTotalElements()).isGreaterThanOrEqualTo(1);

    Optional<GroomerES> optGroomer = searchResult.getContent().stream()
        .filter(grmrEs -> groomerDB.getUuid().equalsIgnoreCase(grmrEs.getUuid())).findFirst();
    
    assertThat(optGroomer.isPresent()).isTrue();
    
    GroomerES gromer = optGroomer.get();
   
    assertThat(gromer.getChargePerMile()).isEqualTo(chargePerMile);
    
    // distanceFromSearch 1.82
    
    assertThat(gromer.getDropOffCost()).isNull();
    assertThat(gromer.getPickUpCost()).isNull();

  }
  
  @Transactional
  @Test
  void itShouldSearch_with_listing_turned_off() throws Exception {

    /**
     * Groomer #1<br>
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     */
    Groomer groomerDB = testEntityGeneratorService.getGroomer();
    groomerDB.setListing(false);
    System.out.println("name: " + groomerDB.getFullName());

    double chargePerMile = 5D;

    groomerDB.setChargePerMile(chargePerMile);
    groomerDB.setOfferedDropOff(false);
    groomerDB.setOfferedPickUp(false);
    
    GroomerES groomer = entityDTOMapper.mapGroomerEntityToGroomerES(groomerDB);

    Address address = new Address();
    address.setCity("Santa Monica");
    address.setState("CA");
    address.setZipcode("90405");
    address.setStreet("3408 Pearl St");
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(null);
    groomer.setAddress(entityDTOMapper.mapAddressToAddressEs(address));

    Long id = RandomGeneratorUtils.getLongWithin(1000, Long.MAX_VALUE-5);
    
    groomer.setId(id);
    groomer.setStatus(GroomerStatus.ACTIVE);
    groomer = groomerESRepository.save(groomer);
    groomers.add(groomer);


    /**
     * Use groomer #1 as starting point, lat: 34.043148, long: -118.4750169<br>
     */
    GroomerSearchParamsDTO filters = new GroomerSearchParamsDTO();
    filters.setLatitude(34.043148);
    filters.setLongitude(-118.4750169);
    filters.setDistance(5);

    CustomPage<GroomerES> searchResult = groomerService.search(filters);
    
    System.out.println("search result");
    System.out.println(searchResult.toString());

    assertThat(searchResult).isNotNull();

    Optional<GroomerES> optGroomer = searchResult.getContent().stream()
        .filter(grmrEs -> id.equals(grmrEs.getId())).findFirst();
    
    assertThat(optGroomer.isPresent()).isFalse();

  }

  @AfterEach
  void cleanUp() {
    groomers.stream().forEach(groomer -> {
      groomerESRepository.deleteById(groomer.getId());
    });
  }
}
