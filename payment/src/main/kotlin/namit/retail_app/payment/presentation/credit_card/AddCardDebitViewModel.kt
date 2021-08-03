package namit.retail_app.payment.presentation.credit_card

import androidx.lifecycle.MutableLiveData
import namit.retail_app.core.presentation.base.BaseViewModel
import namit.retail_app.core.utils.SingleLiveEvent
import namit.retail_app.core.utils.UseCaseResult
import namit.retail_app.payment.domain.AddNewCreditCardUseCase
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import kotlinx.coroutines.launch
import java.util.*

class AddCardDebitViewModel(
    private val omiseClient: Client,
    private val addNewCreditCardUseCase: AddNewCreditCardUseCase
) : BaseViewModel() {

    companion object {
        private const val VALID_LENGTH_OF_CARD_NUMBER = 16
        private const val VALID_LENGTH_OF_CARD_EXPIRE_DATE = 5
        private const val VALID_LENGTH_OF_CARD_CVV_CODE_START = 3
        private const val VALID_LENGTH_OF_CARD_CVV_CODE_END = 4
        private const val VALID_CARD_NAME_WORD_MINIMUM = 2

        private const val PREFIX_JCB_CARD = 35
        private const val PREFIX_VISA_CARD = 4
        private const val PREFIX_MASTER_CARD_START = 51
        private const val PREFIX_MASTER_CARD_END = 55

        private const val CARD_FORM = "•••• •••• •••• ••••"
    }

    val cardName = MutableLiveData<String>()
    val cardNumberHide = MutableLiveData<String>()
    val cardNumber = MutableLiveData<String>()

    val showInvalidCardName = MutableLiveData<Boolean>()
    val showInvalidCardNumber = MutableLiveData<Boolean>()
    val showInvalidCardExpireDate = MutableLiveData<Boolean>()
    val showInvalidCardCvvCode = MutableLiveData<Boolean>()
    val enableAddCard = MutableLiveData<Boolean>()

    val hideCardIcon = MutableLiveData<Unit>()
    val cardIsJCB = MutableLiveData<Unit>()
    val cardIsVisa = MutableLiveData<Unit>()
    val cardIsMasterCard = MutableLiveData<Unit>()

    val isAddCardProcessing = MutableLiveData<Boolean>()
    val dismissDialog = SingleLiveEvent<Unit>()
    val showOtherErrorMessage = SingleLiveEvent<String>()
    val addNewCardSuccess = SingleLiveEvent<Unit>()

    var currentCardName = ""
    var currentCardNumber = ""
    var currentCardExpireDate = ""
    var currentCardCvvCode = ""
    var isDefaultCard = false

    init {
        cardNumberHide.value = CARD_FORM
    }

    fun setCardName(cardName: String) {
        this.cardName.value = cardName
        currentCardName = cardName
        checkDataForEnableAddCard()
    }

    fun setCardNumber(cardNumber: String) {
        if (cardNumber.isBlank()) {
            cardNumberHide.value = CARD_FORM
        } else {

            //Condition for render credit card icon
            detectCardType(cardNumber)

            //Condition for render card number like ๐๐๐๐ ๐๐๐๐ ๐๐๐๐ 1234
            if (cardNumber.length <= 14) {
                val lastDigit = cardNumber.replace(" ", "").last()
                this.cardNumberHide.value = CARD_FORM.replaceRange(
                    cardNumber.length - 1,
                    cardNumber.length,
                    "$lastDigit"
                )
            } else {
                val lastDigits = cardNumber.substring(15, cardNumber.length)
                this.cardNumberHide.value =
                    CARD_FORM.replaceRange(15, 15 + lastDigits.length, lastDigits)
            }
        }
        currentCardNumber = cardNumber.replace(" ", "")
        checkDataForEnableAddCard()
    }

    private fun detectCardType(cardNumber: String) {
        if (cardNumber.isNotEmpty()) {
            if (cardNumber.substring(0, 1).toInt() == PREFIX_VISA_CARD) {
                cardIsVisa.value = Unit
            } else if (cardNumber.length >= 2
                && cardNumber.substring(0, 2).toInt() == PREFIX_JCB_CARD
            ) {
                cardIsJCB.value = Unit
            } else if (cardNumber.length >= 2
                && cardNumber.substring(0, 2).toInt()
                in PREFIX_MASTER_CARD_START..PREFIX_MASTER_CARD_END
            ) {
                cardIsMasterCard.value = Unit
            } else {
                hideCardIcon.value = Unit
            }
        } else {
            hideCardIcon.value = Unit
        }
    }

    private fun validateCardNumber(): Boolean {
        return if (currentCardNumber.isNotEmpty()) {
            if (currentCardNumber.substring(0, 1).toInt() == PREFIX_VISA_CARD) {
                true
            } else if (currentCardNumber.length >= 2
                && currentCardNumber.substring(0, 2).toInt() == PREFIX_JCB_CARD
            ) {
                true
            } else (currentCardNumber.length >= 2
                    && currentCardNumber.substring(0, 2).toInt()
                    in PREFIX_MASTER_CARD_START..PREFIX_MASTER_CARD_END)
        } else {
            false
        }
    }

    private fun validateExpireDate(): Boolean {
        return if (currentCardExpireDate.isNotEmpty()) {
            val month = currentCardExpireDate.substring(0, 2).toInt()
            val year = currentCardExpireDate.substring(3, 5).toInt()
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            month in 1..12 && year >= currentYear.rem(100)
        } else {
            false
        }
    }

    private fun validateCardName(): Boolean {
        return if (currentCardName.isNotEmpty()) {
            val words = currentCardName.trim()
            val numberOfInputWords = words.split("\\s+".toRegex()).size
            numberOfInputWords >= VALID_CARD_NAME_WORD_MINIMUM
        } else {
            false
        }
    }

    fun setCardExpireDate(cardExpireDate: String) {
        currentCardExpireDate = cardExpireDate
        checkDataForEnableAddCard()
    }

    fun setCardCvvCode(cardCvvCode: String) {
        currentCardCvvCode = cardCvvCode
        checkDataForEnableAddCard()
    }

    fun setDefault(isDefault: Boolean) {
        isDefaultCard = isDefault
    }

    private fun checkDataForEnableAddCard() {
        enableAddCard.value =
            (currentCardName.isNotEmpty()
                    && currentCardNumber.length == VALID_LENGTH_OF_CARD_NUMBER
                    && currentCardExpireDate.length == VALID_LENGTH_OF_CARD_EXPIRE_DATE
                    && currentCardCvvCode.length in VALID_LENGTH_OF_CARD_CVV_CODE_START..VALID_LENGTH_OF_CARD_CVV_CODE_END)
    }

    fun validateInputData() {
        val checkCardNumber = validateCardNumber()
        val checkExpireDate = validateExpireDate()
        val checkCardName = validateCardName()
        showInvalidCardExpireDate.value = checkExpireDate.not()
        showInvalidCardNumber.value = checkCardNumber.not()
        showInvalidCardName.value = checkCardName.not()
        if (checkCardNumber && checkExpireDate && checkCardName) {
            addNewCreditCard()
        }
    }

    private fun addNewCreditCard() {
        val expireMonth = currentCardExpireDate.substring(0, 2).toInt()
        val expireYear = "20${currentCardExpireDate.substring(3, 5)}".toInt()

        val cardParam = CardParam(
            name = currentCardName,
            number = currentCardNumber,
            expirationMonth = expireMonth,
            expirationYear = expireYear,
            securityCode = currentCardCvvCode
        )

        val request = Token.CreateTokenRequestBuilder(cardParam).build()
        isAddCardProcessing.value = true
        omiseClient.send(request, object : RequestListener<Token> {
            override fun onRequestSucceed(model: Token) {
                model.id?.let {
                    launch {
                        val addResult = addNewCreditCardUseCase.execute(
                            cardToken = it,
                            isDefault = isDefaultCard
                        )

                        isAddCardProcessing.value = false
                        if (addResult is UseCaseResult.Success) {
                            addNewCardSuccess.call()
                        } else {
                            showOtherErrorMessage.value =
                                (addResult as UseCaseResult.Error).exception.message
                        }
                    }
                } ?: kotlin.run {
                    showOtherErrorMessage.value = ""
                }
                isAddCardProcessing.value = false
            }

            override fun onRequestFailed(throwable: Throwable) {
                isAddCardProcessing.value = false
                showOtherErrorMessage.value = throwable.message
            }
        })

    }
}