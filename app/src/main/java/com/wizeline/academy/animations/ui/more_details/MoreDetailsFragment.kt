package com.wizeline.academy.animations.ui.more_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.annotation.FloatRange
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.wizeline.academy.animations.databinding.MoreDetailsFragmentBinding
import com.wizeline.academy.animations.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreDetailsFragment : Fragment() {

    private var _binding: MoreDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MoreDetailsViewModel by viewModels()
    private val args: MoreDetailsFragmentArgs by navArgs()
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MoreDetailsFragmentBinding.inflate(inflater, container, false)
        binding.ivImageDetailLarge.loadImage(args.imageId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.title.observe(viewLifecycleOwner) { binding.tvTitle.text = it }
        viewModel.content.observe(viewLifecycleOwner) { binding.tvFullTextContent.text = it }
        viewModel.fetchData(args.contentIndex)
        setZoomAnimation()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setZoomAnimation() {
        val scaleXAnimation: SpringAnimation = createSpringAnimation(
            binding.ivImageDetailLarge, SpringAnimation.SCALE_X,
            1f, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_LOW_BOUNCY
        )
        val scaleYAnimation: SpringAnimation = createSpringAnimation(
            binding.ivImageDetailLarge, SpringAnimation.SCALE_Y,
            1f, SpringForce.STIFFNESS_MEDIUM, SpringForce.DAMPING_RATIO_LOW_BOUNCY
        )
        setupPinchToZoom()

        binding.ivImageDetailLarge.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                scaleXAnimation.start()
                scaleYAnimation.start()
            } else {
                scaleXAnimation.cancel()
                scaleYAnimation.cancel()
                scaleGestureDetector.onTouchEvent(event)
            }
            true
        }
    }

    private fun setupPinchToZoom() {
        var scaleFactor = 1f
        scaleGestureDetector = ScaleGestureDetector(
            requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    scaleFactor *= detector.scaleFactor
                    binding.ivImageDetailLarge.scaleX *= scaleFactor
                    binding.ivImageDetailLarge.scaleY *= scaleFactor
                    return true
                }
            }
        )
    }

    private fun createSpringAnimation(
        view: View,
        property: DynamicAnimation.ViewProperty,
        finalPosition: Float,
        @FloatRange(from = 0.1) stiffness: Float,
        @FloatRange(from = 0.1) dampingRation: Float
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val spring = SpringForce(finalPosition)
        spring.stiffness = stiffness
        spring.dampingRatio = dampingRation
        animation.spring = spring
        return animation
    }
}