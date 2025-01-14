package cinescout.di.android

import cinescout.design.DesignModule
import cinescout.details.presentation.DetailsPresentationModule
import cinescout.di.kotlin.CineScoutModule
import cinescout.home.presentation.HomePresentationModule
import cinescout.lists.presentation.ListsPresentationModule
import cinescout.search.presentation.SearchPresentationModule
import cinescout.suggestions.presentation.SuggestionsPresentationModule
import org.koin.dsl.module
import org.koin.ksp.generated.module

val CineScoutAndroidModule = module {
    includes(CineScoutModule)

    includes(DesignModule().module)
    includes(DetailsPresentationModule().module)
    includes(HomePresentationModule().module)
    includes(ListsPresentationModule().module)
    includes(SearchPresentationModule().module)
    includes(SuggestionsPresentationModule)
}
