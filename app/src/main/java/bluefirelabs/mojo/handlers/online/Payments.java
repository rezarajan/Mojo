package bluefirelabs.mojo.handlers.online;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import java.util.HashMap;
import java.util.Map;

import bluefirelabs.mojo.R;

public class Payments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        final CardInputWidget mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        final Map card = new HashMap<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        FloatingActionButton pay = (FloatingActionButton) findViewById(R.id.pay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Card cardToSave = mCardInputWidget.getCard();
                if (cardToSave == null) {
                    //mErrorDialogHandler.showError("Invalid Card Data");
                    Snackbar.make(view, "Invalid Card Data", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    cardToSave.validateNumber();
                    cardToSave.validateCVC();
                    if(!cardToSave.validateCard()) {
                        Snackbar.make(view, "Card Data Invalid", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, "Card Data Valid", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Stripe stripe = new Stripe(view.getContext(), "pk_test_MKkfFv08ZZK0p6I2Uk6lVpaY");
                        stripe.createToken(
                                cardToSave,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        Log.d("token", token.getCard().toString());

                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        String uid = user.getUid();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("/stripe_customers/" + uid + "/sources");
                                        String pushId = reference.push().getKey();     //String
                                        //reference.child(pushId).child("token").setValue(token.getCard());
                                        card.put("object", "card");
                                        card.put("exp_month", cardToSave.getExpMonth());
                                        card.put("exp_year", cardToSave.getExpYear());
                                        card.put("number", cardToSave.getNumber());
                                        card.put("cvc", cardToSave.getCVC());
                                        reference.child(pushId).child("token").updateChildren(card);

                                    }
                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Snackbar.make(view, error.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }
                                }
                        );

                    }
                }
            }
        });
    }

}
