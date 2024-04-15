rootProject.name = "asynchronousPaymentSystem"
include("api")
include("domain")
include("persistence")
include("persistence:mysql")
findProject(":persistence:mysql")?.name = "mysql"
