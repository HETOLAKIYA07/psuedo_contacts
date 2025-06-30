package com.trulloy.pseudocontactapp

import Contact
import com.trulloy.pseudocontactapp.GroupRepository
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.content.ContentResolver
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class AddContactActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private lateinit var avatarImageView: ImageView
    private lateinit var phoneContainer: LinearLayout
    private lateinit var addPhoneButton: ImageButton
    private lateinit var groupSpinner: Spinner

    private val phoneEditTexts = mutableListOf<EditText>()
    private val maxPhones = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        val firstNameEditText = findViewById<EditText>(R.id.firstNameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val birthDateEditText = findViewById<EditText>(R.id.birthDateEditText)
        avatarImageView = findViewById(R.id.avatarImage)
        phoneContainer = findViewById(R.id.phoneContainer)
        addPhoneButton = findViewById(R.id.addPhoneButton)
        groupSpinner = findViewById(R.id.groupSpinner) // You must add Spinner in XML

        val groups = GroupRepository.getAllGroups(this)
        val groupNames = listOf("No Group") + groups.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSpinner.adapter = adapter

        val contactToEdit = intent.getSerializableExtra("contactToEdit") as? Contact

        if (contactToEdit != null) {
            firstNameEditText.setText(contactToEdit.firstName)
            lastNameEditText.setText(contactToEdit.lastName)
            emailEditText.setText(contactToEdit.email)
            birthDateEditText.setText(contactToEdit.birthDate ?: "")

            if (!contactToEdit.imageUri.isNullOrEmpty()) {
                selectedImageUri = Uri.parse(contactToEdit.imageUri)
                avatarImageView.setImageURI(selectedImageUri)
            }

            val phones = listOf(contactToEdit.phone1, contactToEdit.phone2, contactToEdit.phone3, contactToEdit.phone4)
            phones.filterNotNull().forEach {
                addPhoneField(it)
            }



            val selectedGroupIndex = groups.indexOfFirst { it.id == contactToEdit.groupId?.toLong() }
            groupSpinner.setSelection(if (selectedGroupIndex != -1) selectedGroupIndex + 1 else 0)

        } else {
            addPhoneField()
        }

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }

        val pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val savedPath = copyImageToInternalStorage(it)
                if (savedPath != null) {
                    selectedImageUri = Uri.fromFile(File(savedPath))
                    avatarImageView.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        avatarImageView.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        addPhoneButton.setOnClickListener {
            if (phoneEditTexts.size < maxPhones) {
                addPhoneField()
            } else {
                Toast.makeText(this, "Maximum 4 phone numbers allowed", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val selectedGroupIndex = groupSpinner.selectedItemPosition
            val selectedGroupId = if (selectedGroupIndex > 0) groups[selectedGroupIndex - 1].id else null

            val updatedContact = Contact(
                id = contactToEdit?.id ?: 0,
                firstName = firstNameEditText.text.toString().trim(),
                lastName = lastNameEditText.text.toString().trim(),
                phone1 = cleanNumber(phoneEditTexts.getOrNull(0)?.text?.toString()) ?: "",
                phone2 = cleanNumber(phoneEditTexts.getOrNull(1)?.text?.toString()),
                phone3 = cleanNumber(phoneEditTexts.getOrNull(2)?.text?.toString()),
                phone4 = cleanNumber(phoneEditTexts.getOrNull(3)?.text?.toString()),
                email = emailEditText.text.toString().trim(),
                birthDate = birthDateEditText.text.toString().trim(),
                imageUri = selectedImageUri?.toString(),
                groupId = selectedGroupId?.toInt()
            )

            if (contactToEdit == null) {
                ContactRepository.addContact(this, updatedContact)
            } else {
                ContactRepository.updateContact(this, updatedContact)
            }

            finish()
        }
    }

    private fun addPhoneField(prefilledText: String = "") {
        val phoneEditText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
            hint = "Phone Number ${phoneEditTexts.size + 1}"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            textSize = 16f
            setText(prefilledText)
        }
        phoneContainer.addView(phoneEditText)
        phoneEditTexts.add(phoneEditText)
    }

    private fun cleanNumber(number: String?): String? {
        return number?.replace(Regex("[^0-9]"), "")?.takeIf { it.isNotEmpty() }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val fileName = getFileName(uri) ?: return null
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileName(uri: Uri): String? {
        var name: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (name == null) {
            name = uri.path?.substringAfterLast('/')
        }
        return name
    }
}
