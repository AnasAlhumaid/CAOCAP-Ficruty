package sa.gov.ksaa.dal.data.models

data class UploadedFile(
    var file_name: String? = null,
    var file_type: String? = null,
    var file_path: String? = null,
): MyModel()