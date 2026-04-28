package sa.gov.ksaa.dal.data.webservices.dal.responses

import sa.gov.ksaa.dal.data.models.Project

data class GetProjectsResponse(
    val `data`: List<Project?>?
)