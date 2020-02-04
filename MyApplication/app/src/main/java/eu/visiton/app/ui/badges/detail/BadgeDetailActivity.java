package eu.visiton.app.ui.badges.detail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import eu.visiton.app.R;
import eu.visiton.app.materialx.utils.Tools;
import eu.visiton.app.util.Constantes;

public class BadgeDetailActivity extends AppCompatActivity implements BadgeDetailListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_detail);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString(Constantes.EXTRA_BADGE_ID);
        boolean isEarned = extras.getBoolean(Constantes.EXTRA_BADGE_EARNED);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor, new BadgeDetailFragment(id, isEarned))
                .commit();

        initToolbar();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.badge));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }
}
