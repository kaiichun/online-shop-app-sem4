package com.example.onlineshop.ui.fragment.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.databinding.FragmentOrderHistoryBinding
import com.example.onlineshop.ui.adapter.OrderHistoryAdapter
import com.example.onlineshop.ui.viewModel.user.OrderHistoryViewModel
import com.example.onlineshop.ui.viewModel.user.ProfileViewModel

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderHistoryFragment : Fragment() {
    private lateinit var binding: FragmentOrderHistoryBinding
    private val viewModel: OrderHistoryViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        profileViewModel.getCurrentUser()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderHistoryAdapter = OrderHistoryAdapter(emptyList())
        binding.rvOrder.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderHistoryAdapter
        }

        viewModel.run {
            getOrderHistory()
            order.observe(viewLifecycleOwner) { orderHistoryList ->
                orderHistoryAdapter.setOrderHistoryItems(orderHistoryList)
                binding.tvEmptyOrder.visibility = if (orderHistoryList.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        profileViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.getOrderHistory()
            }
        }
    }
}