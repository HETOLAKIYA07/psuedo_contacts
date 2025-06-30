import java.io.Serializable

data class Contact(
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val phone1: String,
    val phone2: String?,
    val phone3: String?,
    val phone4: String?,
    val email: String,
    val birthDate: String?,
    val imageUri: String?,
    val groupId: Int? = null
) : Serializable {
    fun getFullName(): String = "$firstName $lastName"

    fun getInitials(): String {
        val initials = "${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
        return initials.uppercase()
    }

    fun getAvatarColor(): Int {
        val colors = arrayOf(
            0xFF2196F3, 0xFF4CAF50, 0xFFFF9800,
            0xFF9C27B0, 0xFFE91E63, 0xFF00BCD4,
            0xFFFFEB3B, 0xFF795548
        ).map { it.toInt() }

        val hash = (firstName + lastName).hashCode().let { if (it < 0) -it else it }
        return colors[hash % colors.size]
    }
}
