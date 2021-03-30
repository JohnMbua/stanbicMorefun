package com.stanbicagent;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stanbicagent.R;


public class ProdDetails extends Fragment {
ImageView imageView1;
    String head,desc,prodid;
    TextView tvhd,tvds,tvprd;

    public ProdDetails() {
        // Required empty public constructor
    }
  /*  private static Fragment newInstance(Context context) {
        LayoutOne f = new LayoutOne();

        return f;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.proddetail, null);
        tvds = (TextView) root.findViewById(R.id.bnamebc);
        tvhd = (TextView) root.findViewById(R.id.headl);
        imageView1 = (ImageView) root.findViewById(R.id.img);
        Bundle bundle = this.getArguments();
        if (bundle != null) {


            desc = bundle.getString("desc");
            if(desc.equals("disp")){
                head = bundle.getString("head");
                prodid = bundle.getString("content");
                prodid = prodid.replace("\n", "<br>");
                tvds.setText(Html.fromHtml(prodid));
                tvhd.setText(head);
            }
           if(desc.equals("Loans")){
               tvds.setText("Unsecured Personal Loans\n" +
                       "At National Bank, we understand the needs of our customers’ are different that is why we offer tailor made solutions to each of our customers.\n" +
                       "Our unsecured personal loan helps you achieve your dreams – best education for your children, , shopping in Dubai, make improvements on your home ,take advantage of a great investment opportunity, post-graduate studies for yourself, a much-needed overseas holiday for the whole family.\n" +
                       "Your dream is just an application away with National Bank Personal Unsecured Loan.\n" +
                       "\n" +
                       "Eligibility\n" +
                       "\n" +
                       "    You need to derive your income through a salary\n" +
                       "\n" +
                       "Features\n" +
                       "\n" +
                       "    The minimum amount you can borrow is Kshs 50,000; the maximum Kshs 4million.\n" +
                       "    Flexible repayment options – you get up to 6 years to repay the loan.\n" +
                       "    Interest rates calculated on reducing balance basis.\n" +
                       "    No security needed.\n" +
                       "\n" +
                       "More\n" +
                       "\n" +
                       "Benefits\n" +
                       "\n" +
                       "    Competitive interest rates\n" +
                       "    No security needed\n" +
                       "    Credit Life & Retrenchment Insurance cover provided\n" +
                       "    Convenience and easy access; service at your doorstep.\n" +
                       "    Easy access of your money via ATM, Mobile banking and internet banking.\n" +
                       "    Loan top up available.\n" +
                       "\n" +
                       " \n" +
                       "\n" +
                       "Documents Required\n" +
                       "National Bank Customers\n" +
                       "\n" +
                       "    Proof of income – latest 3 months certified copies of pay slips.\n" +
                       "    Copy of ID/ passport.\n" +
                       "    One passport size photograph.\n" +
                       "    Letter of introduction from employer on company letter head.\n" +
                       "\n" +
                       " \n" +
                       "\n" +
                       "Non-National Bank Customers\n" +
                       "\n" +
                       "    Introduction letter from employer on company letterhead\n" +
                       "    Six months original bank statements from current bankers, where salary is processed.\n" +
                       "    Copy of ID / Passport\n" +
                       "    Proof of income – latest 3 months certified copies of pay slips.\n" +
                       "    One passport size photograph\n" +
                       "\n");
               tvhd.setText("Loans");
           }

            if(desc.equals("Invest")){
                tvds.setText("For all your investing needs, invest with the bank by opening a Fixed Deposit account at competitive interest rates and get high returns.\n" +
                        "\n" +
                        "This solution allows you to hold some money with the bank for a defined period of time and in return the bank will pay you interest over this period.\n" +
                        "\n" +
                        "The solutions available are:\n" +
                        "\n" +
                        "Ordinary fixed deposit for various tenors for amounts from KES 20,000.\n" +
                        "High yield fixed deposit for 1 year tenor for amounts from KES 1,000,000 attracting high interest return to you");
                tvhd.setText("Investing");
            }


        }

        return root;
    }



    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }

}
