package com.smality.googlein_app_review;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.*;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private ReviewManager reviewManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        reviewManager = ReviewManagerFactory.create(this);
        findViewById(R.id.btn_rate_app).setOnClickListener(view -> showRateApp());
    }

    /**
     * In-App review API'nin sağladığı rating widget ve alt sayfalarını gosteren metod
     *Rating widget kotalara ve sınırlamalara bağlı olarak gösterilebilir veya gösterilmeyebilir
     */
    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    //Rating widget gösterilme işlemi bitti. Yalnız bu noktada API'de, kullanıcının derecelendirme yapıp yapmadığıyla
                    //ilgili bir kontrol bulunmamaktadır

                });
            } else {
                //Herhangi bir hata varsa showRateAppFallbackDialog metodu çağırılıyor
                showRateAppFallbackDialog();
            }
        });
    }

    /**
     * bir hata ile karşılaşıp, uygulama içinde Rating widget gösterilemediği durumda,
     * uygulamanızı PlayStore yönlendirip, PlayStore web sitesinde derecelendirme yapılmasına yönlendirdim.
     */
    private void showRateAppFallbackDialog() {
        //Ben tercih olarak MaterialAlertDialogBuilder kullandım. Fakat dilerseniz, klasil AlertDialog da kullanabilirsiniz
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> redirectToPlayStore())
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {

                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {

                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    //Kullanıcıyı PlayStore yönlendirme(Hata ile karşılaşıldığında kullandım)
    public void redirectToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}