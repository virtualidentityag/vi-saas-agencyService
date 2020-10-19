package de.caritas.cob.agencyservice.api.controller;

import static java.util.Objects.requireNonNull;

import de.caritas.cob.agencyservice.api.exception.BadRequestException;
import de.caritas.cob.agencyservice.api.model.AgencyResponseDTO;
import de.caritas.cob.agencyservice.api.repository.agency.ConsultingType;
import de.caritas.cob.agencyservice.api.service.AgencyService;
import de.caritas.cob.agencyservice.generated.api.controller.AgenciesApi;
import io.swagger.annotations.Api;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for agency API requests
 *
 */
@RestController
@Api(tags = "agency-controller")
public class AgencyController implements AgenciesApi {

  private final AgencyService agencyService;

  @Autowired
  public AgencyController(AgencyService agencyService) {
    this.agencyService = requireNonNull(agencyService);
  }

  /**
   * Gets a list of AgencyResponseDTOs (from database) and returns the list and a 200 OK on success
   * depending on the post code that is given via query parameter.
   *
   * @param postcode the postcode for regarding agencies
   * @param consultingType the type used to filter the agencies
   * @return the List of agencies with information
   */
  @Override
  public ResponseEntity<List<AgencyResponseDTO>> getAgencies(
      @RequestParam Integer consultingType, @RequestParam String postcode) {
    if (postcode == null || postcode.length() < 3 || postcode.length() > 5) {
      throw new BadRequestException("Postcode size is invalid");
    }

    Optional<ConsultingType> optionalConsultingType = ConsultingType.valueOf(consultingType);
    if (!optionalConsultingType.isPresent()) {
      throw new BadRequestException("Consulting type is invalid");
    }

    List<AgencyResponseDTO> agencies =
        agencyService.getAgencies(postcode, optionalConsultingType.get());

    return !CollectionUtils.isEmpty(agencies)
        ? new ResponseEntity<>(agencies, HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Returns information of the provided agencies.
   *
   * @param agencyIds the List of agency IDs
   * @return the List of agencies with information
   */
  @Override
  public ResponseEntity<List<AgencyResponseDTO>> getAgenciesByIds(
      @PathVariable("agencyIds") List<Long> agencyIds) {

    List<AgencyResponseDTO> agencies = agencyService.getAgencies(agencyIds);

    return agencies.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
        : new ResponseEntity<>(agencies, HttpStatus.OK);
  }
}
