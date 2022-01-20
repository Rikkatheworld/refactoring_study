data class EncapsulateVarOwner(val firstName: String, val lastName: String)
class EncapsulateVar {
    private val defaultOwner = EncapsulateVarOwner(firstName = "Rikka", lastName = "xie")

    private var defaultOwnerData =  EncapsulateVarOwner(firstName = "Rikka", lastName = "xie")

    fun getDefaultOwner(): EncapsulateVarOwner  = defaultOwnerData

    fun setDefaultOwner(owner: EncapsulateVarOwner) {
        defaultOwnerData = owner
    }

}