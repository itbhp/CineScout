package cinescout.lists.presentation.mapper

import cinescout.lists.presentation.previewdata.ListItemUiModelPreviewData
import cinescout.movies.domain.testdata.MovieWithPersonalRatingTestData
import kotlin.test.Test
import kotlin.test.assertEquals

class ListItemUiModelMapperTest {

    private val mapper = ListItemUiModelMapper()

    @Test
    fun `maps to ui model`() {
        // given
        val movieWithPersonalRating = MovieWithPersonalRatingTestData.Inception
        val expected = ListItemUiModelPreviewData.Inception

        // when
        val result = mapper.toUiModel(movieWithPersonalRating)

        // then
        assertEquals(expected, result)
    }
}
