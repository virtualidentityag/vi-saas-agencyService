package de.caritas.cob.agencyservice.api.admin.service.agency;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.caritas.cob.agencyservice.api.model.AgencyAdminFullResponseDTO;
import de.caritas.cob.agencyservice.api.model.AgencyLinks;
import de.caritas.cob.agencyservice.api.model.HalLink.MethodEnum;
import de.caritas.cob.agencyservice.api.repository.agency.Agency;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class AgencyAdminFullResponseDTOBuilderTest {

  AgencyAdminFullResponseDTOBuilder agencyAdminFullResponseDTOBuilder;
  Agency agency;

  @BeforeEach
  public void init() {
    EasyRandom easyRandom = new EasyRandom();
    this.agency = easyRandom.nextObject(Agency.class);
    this.agencyAdminFullResponseDTOBuilder = new AgencyAdminFullResponseDTOBuilder(agency);
  }

  @AfterEach
  void tearDown() {
    ReflectionTestUtils.setField(agencyAdminFullResponseDTOBuilder, "featureDemographicsEnabled", false);
  }

  @Test
  void fromAgency_Should_Return_ValidAgency() {

    var result = agencyAdminFullResponseDTOBuilder.fromAgency();

    assertBaseDTOAttributesAreMapped(result);
  }

  private void assertBaseDTOAttributesAreMapped(AgencyAdminFullResponseDTO result) {
    assertEquals(agency.getId(), result.getEmbedded().getId());
    assertEquals(agency.getName(), result.getEmbedded().getName());
    assertEquals(agency.getDescription(), result.getEmbedded().getDescription());
    assertEquals(agency.isTeamAgency(), result.getEmbedded().getTeamAgency());
    assertEquals(agency.getDioceseId(), result.getEmbedded().getDioceseId());
    assertEquals(agency.getPostCode(), result.getEmbedded().getPostcode());
    assertEquals(agency.getCity(), result.getEmbedded().getCity());
    assertEquals(agency.isOffline(), result.getEmbedded().getOffline());
    assertEquals(agency.getUrl(), result.getEmbedded().getUrl());
    assertEquals(agency.isExternal(), result.getEmbedded().getExternal());
    assertEquals(agency.getConsultingTypeId(), result.getEmbedded().getConsultingType());
    assertEquals(String.valueOf(agency.getCreateDate()), result.getEmbedded().getCreateDate());
    assertEquals(String.valueOf(agency.getUpdateDate()), result.getEmbedded().getUpdateDate());
    assertEquals(String.valueOf(agency.getDeleteDate()), result.getEmbedded().getDeleteDate());
  }

  @Test
  void fromAgency_Should_Return_ValidAgency_ForDemographics() {
    ReflectionTestUtils.setField(agencyAdminFullResponseDTOBuilder, "featureDemographicsEnabled",
        true);
    var result = agencyAdminFullResponseDTOBuilder.fromAgency();
    assertBaseDTOAttributesAreMapped(result);
    assertEquals(toInteger(agency.getAgeFrom()), result.getEmbedded().getDemographics().getAgeFrom());
    assertEquals(toInteger(agency.getAgeTo()), result.getEmbedded().getDemographics().getAgeTo());
    assertEquals(agency.getGender().toString(), result.getEmbedded().getDemographics().getGender());
  }

  private Integer toInteger(Short value) {
    return value != null ? value.intValue() : null;
  }

  @Test
  void fromAgency_Should_Return_ValidHalLinks() {

    AgencyAdminFullResponseDTO result = agencyAdminFullResponseDTOBuilder.fromAgency();
    AgencyLinks agencyLinks = result.getLinks();

    assertThat(result, notNullValue());
    assertThat(agencyLinks.getSelf(), notNullValue());
    assertThat(agencyLinks.getSelf().getMethod(), is(MethodEnum.GET));
    assertThat(agencyLinks.getSelf().getHref(),
        is(String.format("/agencyadmin/agencies/%s", agency.getId())));
    assertThat(agencyLinks.getDelete(), notNullValue());
    assertThat(agencyLinks.getDelete().getMethod(), is(MethodEnum.DELETE));
    assertThat(agencyLinks.getDelete().getHref(),
        is(String.format("/agencyadmin/agencies/%s", agency.getId())));
    assertThat(agencyLinks.getUpdate(), notNullValue());
    assertThat(agencyLinks.getUpdate().getMethod(), is(MethodEnum.PUT));
    assertThat(agencyLinks.getUpdate().getHref(),
        is(String.format("/agencyadmin/agencies/%s", agency.getId())));
    assertThat(agencyLinks.getPostcodeRanges(), notNullValue());
    assertThat(agencyLinks.getPostcodeRanges().getMethod(), is(MethodEnum.GET));
    assertThat(agencyLinks.getPostcodeRanges().getHref(),
        is(String.format("/agencyadmin/postcoderanges/%s", this.agency.getId())));
  }

}
