package kr.nexters.onepage.mypage;

import android.util.Log;

import io.reactivex.functions.Consumer;
import kr.nexters.onepage.R;
import kr.nexters.onepage.common.model.PageRepo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageService {
    private static int resIds[] = {R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert,
            android.R.drawable.ic_delete, android.R.drawable.ic_input_add,
            android.R.drawable.ic_dialog_dialer, android.R.drawable.ic_dialog_email};
    private MyPageAPI myPageAPI = MyPageAPI.Factory.create();

    public void findPageByUser(String email, Integer pageIndex, Integer perPageSize, Consumer<PageRepo> addFunc) {
        if(addFunc == null) {
            return;
        }
        myPageAPI.findPageByUser(email, pageIndex, perPageSize).enqueue(new Callback<PageRepo>() {
            @Override
            public void onResponse(Call<PageRepo> call, Response<PageRepo> response) {
                if (response.isSuccessful()) {
                    try {
                        PageRepo pageRepo = response.body();
                        addFunc.accept(pageRepo);
                    } catch (Exception e) {
                        Log.e("indPageByUser : ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PageRepo> call, Throwable t) {
                Log.e("findPageByUser : ", t.getMessage());
            }
        });
    }

    public void findPageByHeart(String email, Integer pageIndex, Integer perPageSize, Consumer<PageRepo> addFunc) {
        if(addFunc == null) {
            return;
        }
        myPageAPI.findPageByHeart(email, pageIndex, perPageSize).enqueue(new Callback<PageRepo>() {
            @Override
            public void onResponse(Call<PageRepo> call, Response<PageRepo> response) {
                if (response.isSuccessful()) {
                    try {
                        PageRepo pageRepo = response.body();
                        addFunc.accept(pageRepo);
                    } catch (Exception e) {
                        Log.e("findPageByHeart : ", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PageRepo> call, Throwable t) {
                Log.e("findPageByHeart : ", t.getMessage());
            }
        });
    }

}
