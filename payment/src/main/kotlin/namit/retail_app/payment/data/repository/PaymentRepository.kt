package namit.retail_app.payment.data.repository

import namit.retail_app.core.utils.RepositoryResult
import namit.retail_app.payment.data.PaymentMethodModel
import namit.retail_app.payment.enums.CardType
import namit.retail_app.core.enums.PaymentType
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.toDeferred
import hasura.AddCreditCardMutation
import hasura.DeleteUserPaymentMethodMutation
import hasura.GetUserPaymentMethodQuery
import hasura.PaymentMethodAllowQuery
import hasura.fragment.UserPaymentMethodElement

interface PaymentRepository {
    suspend fun getUserPaymentList(
        page: Int
    ): RepositoryResult<List<PaymentMethodModel>>

    suspend fun getAllowPaymentList(): RepositoryResult<List<PaymentMethodModel>>

    suspend fun addNewCreditCard(
        cardToken: String,
        isDefault: Boolean
    ): RepositoryResult<Boolean>

    suspend fun removeUserPaymentMethod(userPaymentMethodId: Int): Boolean
}

class PaymentRepositoryImpl(private val apolloAuth: ApolloClient) : PaymentRepository {

    companion object {
        const val SIZE_PAYMENT_EACH_REQUEST = 20
        const val FIRST_PAGE = 0
        private const val CARD_NUMBER_PREFIX = "**** **** **** "
    }

    override suspend fun getUserPaymentList(
        page: Int
    ): RepositoryResult<List<PaymentMethodModel>> {
        val skip = page * SIZE_PAYMENT_EACH_REQUEST

        val query = GetUserPaymentMethodQuery.builder()
            .skip(skip)
            .limit(SIZE_PAYMENT_EACH_REQUEST).build()
        val deferred = apolloAuth.query(query).toDeferred()
        val response = deferred.await()
        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = mutableListOf<PaymentMethodModel>()
            response.data()?.user_payment_methods()?.forEach {
                result.add(convertToPaymentMethodModel(it.fragments().userPaymentMethodElement()))
            }
            RepositoryResult.Success(result)
        }
    }

    override suspend fun getAllowPaymentList(): RepositoryResult<List<PaymentMethodModel>> {
        val query = PaymentMethodAllowQuery.builder().build()
        val deferred = apolloAuth.query(query).toDeferred()
        val response = deferred.await()
        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = mutableListOf<PaymentMethodModel>()
            response.data()?.payment_methods()?.forEach {
                val paymentType = when (it.slug()) {
                    PaymentType.CASH.value -> PaymentType.CASH
                    PaymentType.CREDIT_CARD.value -> PaymentType.CREDIT_CARD
                    PaymentType.TRUE_MONEY.value -> PaymentType.TRUE_MONEY
                    else -> PaymentType.TRUE_POINT
                }
                result.add(
                    PaymentMethodModel(
                        id = -1,
                        title = it.name(),
                        type = paymentType,
                        paymentMethodId = it.id()
                    )
                )
            }
            RepositoryResult.Success(result)
        }
    }

    override suspend fun addNewCreditCard(
        cardToken: String,
        isDefault: Boolean
    ): RepositoryResult<Boolean> {
        val mutation =
            AddCreditCardMutation.builder().cardToken(cardToken).isDefault(isDefault).build()

        val deferred = apolloAuth.mutate(mutation).toDeferred()
        val response = deferred.await()
        return if (response.hasErrors()) {
            RepositoryResult.Error(response.errors()[0].message().toString())
        } else {
            val result = response.data()?.addCreditCard()
            if (result?.id() != null) {
                RepositoryResult.Success(true)
            } else {
                RepositoryResult.Success(false)
            }
        }
    }

    override suspend fun removeUserPaymentMethod(userPaymentMethodId: Int): Boolean {
        val deferred = apolloAuth.mutate(
            DeleteUserPaymentMethodMutation
                .builder()
                .userPaymentId(userPaymentMethodId)
                .build()
        ).toDeferred()
        val response = deferred.await()
        return response.hasErrors().not()
    }

    private fun convertToPaymentMethodModel(input: UserPaymentMethodElement): PaymentMethodModel {
        val paymentType = when (input.payment_method().slug()) {
            PaymentType.CASH.value -> PaymentType.CASH
            PaymentType.CREDIT_CARD.value -> PaymentType.CREDIT_CARD
            PaymentType.TRUE_MONEY.value -> PaymentType.TRUE_MONEY
            else -> PaymentType.TRUE_POINT
        }

        val cardType = when (input.credit_card_source()?.brand()) {
            CardType.VISA.value -> CardType.VISA
            CardType.MASTER_CARD.value -> CardType.MASTER_CARD
            CardType.JCB.value -> CardType.JCB
            else -> CardType.UNKNOWNS
        }

        var methodTitle = input.payment_method().name()
        if (paymentType == PaymentType.CREDIT_CARD) {
            methodTitle = CARD_NUMBER_PREFIX + input.credit_card_source()?.card_last_digits()
        }

        return PaymentMethodModel(
            id = input.id(),
            title = methodTitle,
            description = input.payment_method().name(),
            type = paymentType,
            cardType = cardType,
            paymentMethodId = input.payment_method().id(),
            isPrimary = input.is_default
        )
    }

}