package akka.technologies.app.scancode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        TextView messageView = findViewById(R.id.message_id);
        String textName;
        Bundle getData = getIntent().getExtras();

        if (getData != null) {
            textName = getData.getString("Name") + " " + getData.getString("LastName");
            textView.setText(textName);
            messageView.setText(getData.getString("Message"));
        }

        findViewById(R.id.go_scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivity(intent);
            }
        });
    }
}
