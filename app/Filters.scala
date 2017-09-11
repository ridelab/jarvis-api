import javax.inject._

import filters._
import play.api.http._
import play.filters.gzip._

@Singleton
class Filters @Inject()(
    gzip: GzipFilter,
    ct: ContentTypeFilter,
    filters: EnabledFilters,
) extends DefaultHttpFilters(filters.filters :+ gzip :+ ct: _*)
