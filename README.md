# StripeGateway

## Non-tested AI Generated AWS Lambda Stripe Payment Gateway

### Test payload for adding balance
```
{
    "body": {
        "storeId": "store1",
        "amount": 100, // 100 cents = €1.00
        "currency": "eur",
        "paymentToken": "tok_visa",
        "description": "Test payment"
    }
}
```
