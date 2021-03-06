package com.example.chatclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.io.File
import com.example.chatclient.databinding.FragmentLoginBinding


class LogInFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(context?.filesDir, "settings.txt")

        if (file.exists()) {
                val nameTextView = view.findViewById<TextView>(R.id.editTextTextPersonName)
                val ipTextView = view.findViewById<TextView>(R.id.editTextIpAddress)

                val lines:List<String> = file.readLines()

                nameTextView.text = lines[0]
                ipTextView.text = lines[1]
            }

        binding.Done.setOnClickListener {
            val nameTextView = view.findViewById<TextView>(R.id.editTextTextPersonName)
            val name = nameTextView.text.toString()

            val ipTextView = view.findViewById<TextView>(R.id.editTextIpAddress)
            val ip = ipTextView.text.toString()

            File(file.toString()).bufferedWriter().use { out ->
                out.write(name)
                out.write("\n")
                out.write(ip)
            }

            Global.userName = name
            Global.serverIpAndPort = ip

            findNavController().navigate(R.id.action_LoginFragment_to_SecondFragment)
        }
    }
}