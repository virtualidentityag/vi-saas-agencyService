package de.caritas.cob.agencyservice.api.admin.service.agencypostcoderange;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import de.caritas.cob.agencyservice.AgencyServiceApplication;
import de.caritas.cob.agencyservice.api.model.PaginationLinks;
import de.caritas.cob.agencyservice.api.model.PostCodeRangeResponseDTO;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgencyServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class AgencyPostCodeRangeAdminServiceIT {

  @Autowired
  private AgencyPostCodeRangeAdminService agencyPostCodeRangeAdminService;

  @Test
  public void findPostCodeRangesForAgency_Should_returnOneResult_When_perPageIsSetToOne() {
    List<PostCodeRangeResponseDTO> postCodeRanges = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(0, 1, 0L)
        .getEmbedded();

    assertThat(postCodeRanges, hasSize(1));
  }

  @Test
  public void findPostCodeRangesForAgency_Should_returnExpectedResult_When_perPageIsSetToOne() {
    PostCodeRangeResponseDTO postCodeRange = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(0, 1, 0L)
        .getEmbedded()
        .iterator()
        .next();

    assertThat(postCodeRange, notNullValue());
    assertThat(postCodeRange.getAgencyId(), notNullValue());
    assertThat(postCodeRange.getId(), notNullValue());
    assertThat(postCodeRange.getPostcodeFrom(), notNullValue());
    assertThat(postCodeRange.getPostcodeTo(), notNullValue());
    assertThat(postCodeRange.getCreateDate(), notNullValue());
    assertThat(postCodeRange.getUpdateDate(), notNullValue());
  }

  @Test
  public void findPostCodeRangesForAgency_Should_returnOneResult_When_pageIsSetToOneAndPerPageIsSetToOne() {
    List<PostCodeRangeResponseDTO> postCodeRanges = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(1, 1, 15L)
        .getEmbedded();

    assertThat(postCodeRanges, hasSize(1));
  }

  @Test
  public void findPostCodeRangesForAgency_Should_returnOneResult_When_paginationParamsAreZero() {
    List<PostCodeRangeResponseDTO> postCodeRanges = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(0, 0, 0L)
        .getEmbedded();

    assertThat(postCodeRanges, hasSize(1));
  }

  @Test
  public void findPostCodeRangesForAgency_Should_returnOneResult_When_paginationParamsAreNegative() {
    List<PostCodeRangeResponseDTO> postCodeRanges = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(-100, -10, 0L)
        .getEmbedded();

    assertThat(postCodeRanges, hasSize(1));
  }


  @Test
  public void findPostCodeRangesForAgency_Should_returnPaginatedEntities_When_paginationParamsAreSplitted() {
    List<PostCodeRangeResponseDTO> firstPage = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(1, 20, 15L)
        .getEmbedded();
    List<PostCodeRangeResponseDTO> secondPage = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(2, 20, 15L)
        .getEmbedded();

    assertThat(firstPage, hasSize(20));
    assertThat(secondPage, hasSize(10));
  }

  @Test
  public void findPostCodeRangesForAgency_Should_haveExpectedLinks_When_AllParamsAreProvided() {
    PaginationLinks paginationLinks = this.agencyPostCodeRangeAdminService
        .findPostCodeRangesForAgency(2, 2, 15L).getLinks();

    assertThat(paginationLinks.getSelf(), notNullValue());
    assertThat(paginationLinks.getSelf().getHref(),
        endsWith("/agencyadmin/agency/15/postcoderanges?page=2&perPage=2"));
    assertThat(paginationLinks.getNext(), notNullValue());
    assertThat(paginationLinks.getNext().getHref(),
        endsWith("/agencyadmin/agency/15/postcoderanges?page=3&perPage=2"));
    assertThat(paginationLinks.getPrevious(), notNullValue());
    assertThat(paginationLinks.getPrevious().getHref(),
        endsWith("/agencyadmin/agency/15/postcoderanges?page=1&perPage=2"));
  }
}