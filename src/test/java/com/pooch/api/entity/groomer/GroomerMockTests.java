package com.pooch.api.entity.groomer;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.InputStream;
import java.util.Optional;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.pooch.api.elastic.repo.AddressES;
import com.pooch.api.elastic.repo.GroomerES;
import com.pooch.api.entity.address.Address;
import com.pooch.api.utils.MathUtils;
import com.pooch.api.utils.ObjectUtils;
import com.pooch.api.utils.TestEntityGeneratorService;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.json.JSONObject;
import org.json.JSONTokener;

class GroomerMockTests {

  private TestEntityGeneratorService generatorService = new TestEntityGeneratorService();

  @Test
  void checkForSearchLocationFilters() {
    /**
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     * 
     */

    /**
     * 1116 Stanford St, Santa Monica, CA 90403<br>
     * lat: 34.0400821, -118.475029<br>
     */

    double distanceFromMainGroomer =
        MathUtils.distance(34.043148, 34.0400821, -118.4750169, -118.475029);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * 3408 Pearl St, Santa Monica, CA 90405<br>
     * lat: 34.0251161, -118.4517642<br>
     */

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    /**
     * 12107 Palms Blvd, Los Angeles, CA 90066<br>
     * lat: 34.0124107, long: -118.4355353<br>
     */

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0124107, -118.4750169, -118.4355353);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

  }

  @Test
  void filterAddresses() {
    /**
     * 1043 Franklin St, Santa Monica, CA 90403<br>
     * lat: 34.043148, long: -118.4750169<br>
     * 
     */

    /**
     * 1116 Stanford St, Santa Monica, CA 90403<br>
     * lat: 34.0400821, -118.475029<br>
     */

    double distanceFromMainGroomer =
        MathUtils.distance(34.043148, 34.0400821, -118.4750169, -118.475029);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    GroomerES groomer = new GroomerES();

    AddressES address = new AddressES();
    address.setLatitude(34.043148);
    address.setLongitude(-118.4750169);
    groomer.setAddress(address);
    /**
     * 3408 Pearl St, Santa Monica, CA 90405<br>
     * lat: 34.0251161, -118.4517642<br>
     */

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0251161, -118.4750169, -118.4517642);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    address = new AddressES();
    address.setLatitude(34.0251161);
    address.setLongitude(-118.4517642);
    groomer.setAddress(address);

    /**
     * 12107 Palms Blvd, Los Angeles, CA 90066<br>
     * lat: 34.0124107, long: -118.4355353<br>
     */

    distanceFromMainGroomer = MathUtils.distance(34.043148, 34.0124107, -118.4750169, -118.4355353);
    System.out.println("distanceFromMainGroomer: " + distanceFromMainGroomer);

    address = new AddressES();
    address.setLatitude(34.0124107);
    address.setLongitude(-118.4355353);
    groomer.setAddress(address);

    groomer.generateCalculatedValues(new GeoPoint(34.043148, -118.4750169), 3);

    System.out.println("groomer: " + ObjectUtils.toJson(groomer));
  }

  @Test
  void test_paymentintent_key_initial() {
    String paymentIntentId = "pi_3Kzc5SCRM62QoG6s0nGsO2fl";

    assertThat(paymentIntentId.substring(0, 3)).isEqualToIgnoringCase("pi_");
  }

  // @Test
  // void test_json_schema() {
  // try {
  //
  // String schemaJson = "{\n" + " \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n"
  // + " \"title\": \"Product\",\n"
  // + " \"description\": \"A product from the catalog\",\n" + " \"type\": \"object\",\n"
  // + " \"properties\": {\n" + " \"id\": {\n"
  // + " \"description\": \"The unique identifier for a product\",\n"
  // + " \"type\": \"integer\"\n" + " },\n" + " \"name\": {\n"
  // + " \"description\": \"Name of the product\",\n"
  // + " \"type\": \"string\"\n" + " },\n" + " \"price\": {\n"
  // + " \"type\": \"number\",\n" + " \"minimum\": 0,\n"
  // + " \"exclusiveMinimum\": true\n" + " }\n" + " },\n"
  // + " \"required\": [\"id\", \"name\", \"price\"]\n" + "}";
  //
  //
  // JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaJson));
  //
  // System.out.println("jsonSchema: " + jsonSchema.toString());
  //
  // String input = "{\n" + " \"id\": 1,\n" + " \"name\": \"Lampshade\",\n"
  // + " \"price\": 10\n" + "}";
  //
  // JSONObject jsonSubject = new JSONObject(new JSONTokener(input));
  //
  //
  // System.out.println("jsonSubject: " + jsonSubject.toString());
  //
  // Schema schema = SchemaLoader.load(jsonSchema, new DefaultSchemaClient());
  // schema.validate(jsonSubject);
  // } catch (Exception e) {
  // // TODO: handle exception
  // }
  // }
}
