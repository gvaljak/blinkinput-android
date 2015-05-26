package com.microblink.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.microblink.activity.BlinkOCRActivity;
import com.microblink.help.HelpActivity;
import com.microblink.recognizers.ocr.blinkocr.parser.generic.AmountParserSettings;
import com.microblink.recognizers.ocr.blinkocr.parser.generic.IbanParserSettings;


public class MainActivity extends Activity {

    private static final int BLINK_OCR_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void advancedIntegration(View v) {
        // advanced integration example is given in ScanActivity source code
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    /**
     * Called as handler for "simple integration" button.
     */
    public void simpleIntegration(View v) {
        /**
         * In this simple example we will use BlinkOCR SDK to create a simple app
         * that scans an amount from invoice, tax amount from invoice and IBAN
         * to which amount has to be paid.
         */

        Intent intent = new Intent(this, BlinkOCRActivity.class);
        intent.putExtra(BlinkOCRActivity.EXTRAS_LICENSE_KEY, "DB2H5WX3-T2MN76CJ-ZIO5SIWW-MKYTEYZT-UGBW36CJ-ZIMR3WJC-2ZRLCMTD-GPKSSHGK");

        // we need to scan 3 items, so we will add 3 scan configurations to scan configuration array
        ScanConfiguration conf[] = new ScanConfiguration[] {
                // each scan configuration contains two string resource IDs: string shown in title bar and string shown
                // in text field above scan box. Besides that, it contains name of the result and settings object
                // which defines what will be scanned.
                new ScanConfiguration(R.string.amount_title, R.string.amount_msg, "TotalAmount", new AmountParserSettings()),
                new ScanConfiguration(R.string.tax_title, R.string.tax_msg, "Tax", new AmountParserSettings()),
                new ScanConfiguration(R.string.iban_title, R.string.iban_msg, "IBAN", new IbanParserSettings())
        };

        intent.putExtra(BlinkOCRActivity.EXTRAS_SCAN_CONFIGURATION, conf);

        // optionally, if we want the help screen to be available to user on camera screen,
        // we can simply prepare an intent for help activity and pass it to BlinkOCRActivity
        Intent helpIntent = new Intent(this, HelpActivity.class);
        intent.putExtra(BlinkOCRActivity.EXTRAS_HELP_INTENT, helpIntent);

        // once intent is prepared, we start the BlinkOCRActivity which will preform scan and return results
        // by calling onActivityResult
        startActivityForResult(intent, BLINK_OCR_REQUEST_CODE);
    }

    /**
     * This method is called whenever control is returned from activity started with
     * startActivityForResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // first we need to check that we have indeed returned from BlinkOCRActivity with
        // success
        if(requestCode == BLINK_OCR_REQUEST_CODE && resultCode == BlinkOCRActivity.RESULT_OK) {
            // now we can obtain bundle with scan results
            Bundle result = data.getBundleExtra(BlinkOCRActivity.EXTRAS_SCAN_RESULTS);

            // each result is stored under key equal to the name of the scan configuration that generated it
            String totalAmount = result.getString("TotalAmount");
            String taxAmount = result.getString("Tax");
            String iban = result.getString("IBAN");

            Toast.makeText(this, "To IBAN: " + iban + " we will pay total " + totalAmount + ", tax: " + taxAmount, Toast.LENGTH_LONG).show();
        }
    }
}