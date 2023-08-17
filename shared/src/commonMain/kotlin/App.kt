import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import model.IslandDataClass
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import kotlin.math.absoluteValue
import kotlin.math.min


@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val listOfIsland = listOf(
            IslandDataClass(1, "pic1.jpg"),
            IslandDataClass(2, "pic2.jpg"),
            IslandDataClass(3, "pic3.jpg"),
            IslandDataClass(4, "pic4.jpg"),
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

        Column(
            modifier = Modifier.padding(top = 18.dp, bottom = 18.dp, start = 12.dp, end = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CubeInDepthTransition(listOfIsland, bitMapImages.value)
//            GateAnimation(listOfIsland, bitMapImages.value)

//            CubePager(listOfIsland, bitMapImages.value)

//            FadeAnimation(listOfIsland, bitMapImages.value)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CubePager(itemList: List<IslandDataClass>, image: Map<Int, ImageBitmap>) {
    val state = rememberPagerState()

    val scale by remember {
        derivedStateOf {
            1f - (state.currentPageOffsetFraction.absoluteValue) * .3f
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .clip(RoundedCornerShape(30.dp)),
        contentAlignment = Alignment.Center
    ) {
        val offsetFromStart = state.calculateCurrentOffsetForPage(0).absoluteValue
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, 150.dp.roundToPx()) }
                .scale(scaleX = .6f, scaleY = .24f)
                .scale(scale)
                .rotate(offsetFromStart * 90f)
                .blur(
                    radius = 110.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded,
                )
                .background(Color.Black.copy(alpha = .5f))
                .clip(RoundedCornerShape(30.dp))
        )

        HorizontalPager(
            pageCount = itemList.size,
            state = state,
            pageSize = PageSize.Fill
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val pageOffset = state.calculateCurrentOffsetForPage(page)
                        val offScreenRight = pageOffset < 0f
                        val deg = 105f
                        val interpolated = FastOutLinearInEasing.transform(pageOffset.absoluteValue)
                        rotationY = min(interpolated * if (offScreenRight) deg else -deg, 90f)

                        transformOrigin = TransformOrigin(
                            pivotFractionX = if (offScreenRight) 0f else 1f,
                            pivotFractionY = .5f
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(07f)
                        .background(Color.LightGray)
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    image[itemList[page].id]?.let {
                        Image(
                            bitmap = it, // Retrieve the ImageBitmap from the map using the item's id as the key
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(30.dp)),
                        )
                    }
                }
            }
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

                    val offScreenRight = pageOffset < 0f
                    val deg = 105f
                    val interpolated = FastOutLinearInEasing.transform(pageOffset.absoluteValue)
                    rotationY = min(interpolated * if (offScreenRight) deg else -deg, 90f)

                    transformOrigin = TransformOrigin(
                        pivotFractionX = if (offScreenRight) 0f else 1f,
                        pivotFractionY = .5f
                    )
                    /* // translate the contents by the size of the page, to prevent the pages from sliding in from left or right and stays in the center
                     translationX = pageOffset * size.width
                     // apply an alpha to fade the current page in and the old page out
                     alpha = 1 - pageOffset.absoluteValue*/
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GateAnimation(itemList: List<IslandDataClass>, image: Map<Int, ImageBitmap>) {
    val pagerState = rememberPagerState()

    HorizontalPager(
        pageCount = itemList.size,
        state = pagerState,
        pageSize = PageSize.Fill
    ) { index ->

        Box(
            modifier = Modifier.fillMaxSize().pagerGateTransition(index, pagerState)
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

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerGateTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    // Calculate the absolute offset for the current page from the
    // scroll position.
    val pageOffset = pagerState.calculateCurrentOffsetForPage(page)
    translationX = pageOffset * size.width

    if (pageOffset < -1f) {
        // page is far off screen
        alpha = 0f
    } else if (pageOffset <= 0) {
        // page is to the right of the selected page or the selected page
        alpha = 1f
        transformOrigin = TransformOrigin(1f, 0.5f)
        rotationY = -90f * pageOffset.absoluteValue

    } else if (pageOffset <= 1) {
        // page is to the left of the selected page
        alpha = 1f
        transformOrigin = TransformOrigin(0f, 0.5f)
        rotationY = 90f * pageOffset.absoluteValue
    } else {
        alpha = 0f
    }
}

// extension method for current page offset
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.calculateCurrentOffsetForPage(page: Int): Float {
    return (currentPage - page) + currentPageOffsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CubeInDepthTransition(
    itemList: List<IslandDataClass>,
    image: Map<Int, ImageBitmap>
) {
    val pagerState = rememberPagerState()
    HorizontalPager(
        pageCount = itemList.size,
        state = pagerState,
        pageSize = PageSize.Fill
    ) { page ->
        Box(
            Modifier
                .pagerCubeInDepthTransition(page, pagerState)
                .fillMaxSize()
        ) {
            image[itemList[page].id]?.let {
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

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerCubeInDepthTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    cameraDistance = 32f
    // Calculate the absolute offset for the current page from the
    // scroll position.
    val pageOffset = pagerState.calculateCurrentOffsetForPage(page)

    if (pageOffset < -1f) {
        // page is far off screen
        alpha = 0f
    } else if (pageOffset <= 0) {
        // page is to the right of the selected page or the selected page
        alpha = 1f
        transformOrigin = TransformOrigin(0f, 0.5f)
        rotationY = -90f * pageOffset.absoluteValue

    } else if (pageOffset <= 1) {
        // page is to the left of the selected page
        alpha = 1f
        transformOrigin = TransformOrigin(1f, 0.5f)
        rotationY = 90f * pageOffset.absoluteValue
    } else {
        alpha = 0f
    }

    if (pageOffset.absoluteValue <= 0.5) {
        scaleY = 0.4f.coerceAtLeast(1 - pageOffset.absoluteValue)
    } else if (pageOffset.absoluteValue <= 1) {
        scaleY = 0.4f.coerceAtLeast(1 - pageOffset.absoluteValue)
    }
}


expect fun getPlatformName(): String