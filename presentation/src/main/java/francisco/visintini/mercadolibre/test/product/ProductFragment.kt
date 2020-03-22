package francisco.visintini.mercadolibre.test.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import dagger.android.support.AndroidSupportInjection
import francisco.visintini.mercadolibre.test.R
import francisco.visintini.mercadolibre.test.di.withFactory
import francisco.visintini.mercadolibre.test.extensions.setVisible
import francisco.visintini.mercadolibre.test.product.ProductIntent.ImageGalleryPositionChanged
import francisco.visintini.mercadolibre.test.product.ProductViewState.State.Content
import francisco.visintini.mercadolibre.test.product.ProductViewState.State.Loading
import francisco.visintini.mercadolibre.test.product.imagegallery.ProductImageGalleryItem
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import kotlin.contracts.ExperimentalContracts
import kotlinx.android.synthetic.main.fragment_product.*

@ExperimentalContracts
class ProductFragment : Fragment(R.layout.fragment_product) {
    @Inject
    lateinit var productViewModelFactory: ProductViewModel.Factory

    private val disposable = CompositeDisposable()
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val productViewModel: ProductViewModel by viewModels {
        withFactory(
            productViewModelFactory
        )
    }
    private val args: ProductFragmentArgs by navArgs()
    lateinit var viewPagerCallback: ViewPager2.OnPageChangeCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view_product_view_pager.adapter = adapter
        subscribeToImageGalleryIntents()
    }

    override fun onStart() {
        super.onStart()
        productViewModel.viewState.observe(this, Observer(::render))
        productViewModel.start(args.productId)
    }

    private fun render(viewState: ProductViewState) {
        when (viewState.productState) {
            is Loading -> { // TODO put placeholders
            }
            is Content -> {
                with(viewState.productState) {
                    view_product_title.text = title
                    adapter.update(imageGalleryViewState.productImageGalleryItemViewStates.map {
                        ProductImageGalleryItem(it)
                    })
                    if (view_product_view_pager.currentItem != // Prevents scrolling when configuration changes occur
                        imageGalleryViewState.imagePosition
                    ) {
                        view_product_view_pager.setCurrentItem(
                            imageGalleryViewState.imagePosition,
                            false
                        )
                    }
                    view_product_price.text = price
                    view_product_availability.text = availability
                    warranty?.let { view_product_warranty.text = it }
                        ?: view_product_warranty.setVisible(false)
                    condition?.let { view_product_condition.text = it }
                        ?: view_product_condition.setVisible(false)
                }
            }
        }
    }

    override fun onDestroyView() {
        disposable.clear()
        view_product_view_pager.unregisterOnPageChangeCallback(viewPagerCallback)
        super.onDestroyView()
    }

    private fun subscribeToImageGalleryIntents() {
        viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                productViewModel.handleIntent(ImageGalleryPositionChanged(position))
            }
        }
        view_product_view_pager.registerOnPageChangeCallback(viewPagerCallback)
    }
}