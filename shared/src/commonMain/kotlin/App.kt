import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import model.IslandDataClass
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import kotlin.math.absoluteValue

@OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
@Composable
fun App() {
    MaterialTheme {
        val listOfIsland = listOf(
            IslandDataClass(1, "pic1.jpg"),
            IslandDataClass(2, "pic2.jpg"),
            IslandDataClass(3, "pic3.jpg"),
            IslandDataClass(4,  "pic4.jpg"),
            IslandDataClass(5, "pic5.jpg"),
            IslandDataClass(6, "pic6.jpg"),
            IslandDataClass(7, "pic7.jpg")
        )
        val bitMapImages = remember { mutableStateOf<Map<Int, ImageBitmap>>(emptyMap()) }


        LaunchedEffect(Unit) {
            val imagesMap = mutableMapOf<Int, ImageBitmap>()
            for (item in listOfIsland) {
                imagesMap[item.id] = resource(item.resImage).readBytes().toImageBitmap()
            }
            bitMapImages.value = imagesMap
        }





        Column(modifier = Modifier.padding(top = 18.dp, bottom = 18.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
            FadeAnimation(listOfIsland, bitMapImages.value)
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FadeAnimation(itemList: List<IslandDataClass>, image: Map<Int, ImageBitmap>) {
    val pagerState = rememberPagerState()

    HorizontalPager(
        pageCount = itemList.size,
        state = pagerState,
        pageSize = PageSize.Fill
    ) { index ->

        Box(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer {
                    val pageOffset = pagerState.calculateCurrentOffsetForPage(index)
                    // translate the contents by the size of the page, to prevent the pages from sliding in from left or right and stays in the center
                    translationX = pageOffset * size.width
                    // apply an alpha to fade the current page in and the old page out
                    alpha = 1 - pageOffset.absoluteValue
                }
        ) {
            image[itemList[index].id]?.let {
                Image(
                    bitmap = it, // Retrieve the ImageBitmap from the map using the item's id as the key
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(30.dp)),
                )
            }

        }
    }
}

// extension method for current page offset
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

expect fun getPlatformName(): String