import kotlinx.serialization.Serializable

@Serializable
data class ShoppingListItem(val name: String, val priority: Int) {
    val id = name.hashCode();

    companion object {
        const val path = "/shoppingList"
    }

}