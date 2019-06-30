package me.lynnchurch.cipher;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity
{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SHARED_PREFERENCES_NAME = "config";
    public static final String PREFERENCES_ITEM_SELECTED_CIPHETEXT_STYLE_POSITION = "selected_ciphetext_style_position";
    private static final int POSITION_OX = 0; // 16进制
    private static final int POSITION_EMOJI = 1; // emoji
    private static final int POSITION_SPECIAL = 2; // 特殊符号
    private static final Map<String, String> oxMap = new HashMap<>();
    private static final Map<String, String> emojiMap = new HashMap<>(16);
    private static final Map<String, String> specialMap = new HashMap<>(16);
    private static Map<String, String> styleMap = oxMap;

    private Toolbar toolbar;
    private Spinner spinner;
    private TextInputEditText titietPassword;
    private EditText etContent;
    private Button btnEncrypt;
    private Button btnDecrypt;
    private SharedPreferences sharedPreferences;

    static
    {
        emojiMap.put("0", "\uD83D\uDE00");
        emojiMap.put("1", "\uD83D\uDE02");
        emojiMap.put("2", "\uD83D\uDE05");
        emojiMap.put("3", "\uD83D\uDE0E");
        emojiMap.put("4", "\uD83D\uDC36");
        emojiMap.put("5", "\uD83D\uDC31");
        emojiMap.put("6", "\uD83D\uDE48");
        emojiMap.put("7", "\uD83D\uDC37");
        emojiMap.put("8", "\uD83C\uDF4E");
        emojiMap.put("9", "\uD83C\uDF4D");
        emojiMap.put("A", "\uD83C\uDF49");
        emojiMap.put("B", "\uD83E\uDDC0");
        emojiMap.put("C", "\uD83D\uDE97");
        emojiMap.put("D", "\uD83D\uDEB2");
        emojiMap.put("E", "\uD83D\uDE80");
        emojiMap.put("F", "\uD83D\uDE81");

        specialMap.put("0", "&");
        specialMap.put("1", "*");
        specialMap.put("2", "#");
        specialMap.put("3", "%");
        specialMap.put("4", "@");
        specialMap.put("5", "[");
        specialMap.put("6", "?");
        specialMap.put("7", "]");
        specialMap.put("8", "=");
        specialMap.put("9", ")");
        specialMap.put("A", "<");
        specialMap.put("B", "(");
        specialMap.put("C", "^");
        specialMap.put("D", "+");
        specialMap.put("E", "_");
        specialMap.put("F", "|");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initSpinner();
        titietPassword = findViewById(R.id.tietPassword);
        etContent = findViewById(R.id.etContent);
        btnEncrypt = findViewById(R.id.btnEncrypt);
        btnDecrypt = findViewById(R.id.btnDecrypt);
        btnEncrypt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!validate())
                {
                    return;
                }
                try
                {
                    String password = titietPassword.getText().toString();
                    String content = etContent.getText().toString();
                    String ciphetextStyleContent = toCiphetextStyleContent(AESCrypt.encrypt(content, password), styleMap);
                    etContent.setText(ciphetextStyleContent);
                } catch (Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        btnDecrypt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!validate())
                {
                    return;
                }
                String password = titietPassword.getText().toString();
                String content = etContent.getText().toString();
                String decryptContent = null;
                try
                {
                    decryptContent = AESCrypt.decrypt(fromCiphetextStyleContent(content, styleMap), password);
                } catch (Exception e)
                {
                    Log.e(TAG, e.getMessage());
                }
                if (TextUtils.isEmpty(decryptContent))
                {
                    toast(R.string.password_is_wrong);
                } else
                {
                    etContent.setText(decryptContent);
                }
            }
        });
    }

    private void initSpinner()
    {
        spinner = findViewById(R.id.spinner);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.ciphetext_style, R.layout.spinner_ciphetext_style_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                switch (position)
                {
                    case POSITION_OX:
                        styleMap = oxMap;
                        break;
                    case POSITION_EMOJI:
                        styleMap = emojiMap;
                        break;
                    case POSITION_SPECIAL:
                        styleMap = specialMap;
                        break;
                    default:
                        styleMap = oxMap;
                }
                sharedPreferences.edit().putInt(PREFERENCES_ITEM_SELECTED_CIPHETEXT_STYLE_POSITION, position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Log.d(TAG, "onNothingSelected");
            }
        });
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int currentSelectedStylePosition = sharedPreferences.getInt(PREFERENCES_ITEM_SELECTED_CIPHETEXT_STYLE_POSITION, POSITION_OX);
        spinner.setSelection(currentSelectedStylePosition);
    }


    private boolean validate()
    {
        String password = titietPassword.getText().toString();
        String content = etContent.getText().toString();
        if (TextUtils.isEmpty(content))
        {
            toast(R.string.please_input_plaintext_or_ciphetext);
            return false;
        }
        if (TextUtils.isEmpty(password))
        {
            toast(R.string.please_input_password);
            return false;
        }
        return true;
    }

    private void toast(int msgResId)
    {
        Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
    }

    private void toast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 原文转换成带样式的密文
     *
     * @param content  原文
     * @param styleMap 样式映射表
     * @return
     */
    private String toCiphetextStyleContent(String content, Map<String, String> styleMap)
    {
        if (TextUtils.isEmpty(content) || null == styleMap || styleMap.isEmpty())
        {
            return content;
        }
        Set<String> keys = styleMap.keySet();
        for (String key : keys)
        {
            content = content.replace(key, styleMap.get(key));
        }
        return content;
    }

    /**
     * 带样式的密文还原成原文
     *
     * @param styleContent 带样式的密文
     * @param styleMap     样式映射表
     * @return
     */
    private String fromCiphetextStyleContent(String styleContent, Map<String, String> styleMap)
    {
        if (TextUtils.isEmpty(styleContent) || null == styleMap || styleMap.isEmpty())
        {
            return styleContent;
        }
        Set<String> keys = styleMap.keySet();
        for (String key : keys)
        {
            styleContent = styleContent.replace(styleMap.get(key), key);
        }
        return styleContent;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                moveTaskToBack(false);
                return true;

        }
        return super.onKeyUp(keyCode, event);
    }
}
