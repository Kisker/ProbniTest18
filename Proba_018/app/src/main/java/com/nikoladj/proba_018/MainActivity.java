package com.nikoladj.proba_018;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private List<String> drawerItems;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton fab;
    private AlertDialog dialog;
    public static final int NOTIF_ID = 101;
    public static final String NOTIF_CHANNEL_ID = "nas_notif_kanal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Poziv metoda se tek na kraju ovde doda, kada vec unapred odredimo metode
        setupToolbar();
        fillData();
        setupDrawer();
        setupFab();
        //createNotificationChannel();
        showProbaFragment();
    }
    //Ovo je Toolbar koji obvezno mora biti deo ove aplikacije, jer on podesava da nam pokaze Drawer (sandwich)  i listu Drawera
    private void setupToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }
    }
//Ovo nam pokazuje nazive lista data Drawera, kada kliknemo na sandwich
    private void fillData(){
        drawerItems = new ArrayList<>();
        drawerItems.add("Toast");
        drawerItems.add("Snackbar");
        drawerItems.add("Dialog");
        drawerItems.add("Notifications");
        drawerItems.add("Preferences");
    }
    //Ovo nam pokazuje Drawer tittle listu i uvek setup ide na ovakav nacin. Kada kliknemo na jednu specifikaciju, on nam otvori novi list
    //Pozive za R.id moramo da uradimo u main_activity.xml
    private void setupDrawer(){
        //DrawerList je deo drawerLayouta i jedno bez drugog ne ide. Kada u rasporedu fijoka (drawerLayout) odredite listu fijoka
        // (drawerList)- visinu, sirinu, boju - na kraju pozivate metodu drawerItems, kako bi vam iscitala nazive fijoka!
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            // Kada kliknemo na sam naziv sadrzaja, sledeca metoda ce nam dozvoliti ili nece da udjemo u novi fragment.
            //U slucaju da, na primer kliknemo Toast, a pritom nismo definisali tu metodu, na ekranu ce nam se prikazati
            //Unknown. Zato u ovom slucaju koristimo switch/case metodu, odmah u okviru setupDrawer metode!
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Toast";
                        showToast();
                        break;
                    case 1:
                        title = "Snackbar";
                        showSnackbar();
                        break;
                    case 2:
                        title = "Dialog";
                        showDialog();
                        break;
                    case 3:
                        title = "Notification";
                        showNotification();
                        break;
                    case 4:
                        title = "Preferences";
                        showPrefs();
                        break;
                    default:
                        break;
                }
                //drawerList.setItemChecked(i, true);
                //Kada nadjemo i kliknemo na odredjeni sadrzaj, fijoka i sandwich se zatvaraju
                setTitle(title);
                drawerLayout.closeDrawer(drawerList);
            }
        });

//Ovaj ActionDrawerToggle je je specijalna vrsta DrawerListenera, koji nam omogucava da pozovemo metodu otvaranja i zatvaranja fijoke
        //Bez ovog koda, ne bismo mogli otvoriti sandwich na Action baru ili Toolbaru, kako bi nam izbacio celu listu fijoke.
        //Ova metoda je bitna i uvek se radi posle gore navedenih metoda u okviru setupDrawer-a!!!
        drawerToggle = new ActionBarDrawerToggle(
                this,                           /* host Activity */
                drawerLayout,                   /* DrawerLayout object */
                toolbar,                        /* nav drawer image to replace 'Up' caret */
                R.string.app_name,           /* "open drawer" description for accessibility */
                R.string.app_name           /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle("");
                invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle("");
                invalidateOptionsMenu();        // Creates call to onPrepareOptionsMenu()
            }
        };
    }

    //Ovo nam otvara fijoku u novi fragment te ispisuje Toast poruku, kada kliknemo na Toast Drawer.
    private void showToast(){
        Toast.makeText(this, "Ovo je Toast", Toast.LENGTH_SHORT).show();
    }

    //Ovo nam pokazuje Snackbar na aplikaciji, koja se nalazi na donjem horizontalnom uglu same aplikacije.
    //Ona moze da traje neograniceno. Snackbar ne koristi context, uvek trazi View (findViewById)!
    private void showSnackbar(){
        //root u activity_main.xml pripada drawer_layoutu, a to znaci da kada otvorimo Snackbar, da sam koren ekrana
        // bude u mogucnosti da otvori novi fragment na istom ekranu. Bez roota ne bismo bili u mogucnosti da vidimo
        // sadrzaj samog Snackbara. U drugim metodatama (Toast, Dialog i Notification) nismo se trazili ViewById.root,
        //jer nismo imali sta da prikazemo na ekranu!
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.root), "Ovo je Snackbar", Snackbar.LENGTH_SHORT);
        //Umesto View.OnClickListener mozemo koristiti poziv/interfejs UndoListener!
        snackbar.setAction("U Redu", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

// Ovo nam pokazuje Dijalog - klasa NasDijalog i pop-up window unutar toga
    private void showDialog(){
        if (dialog == null){
            dialog = new NasDijalog(this).prepareDialog();
        } else {
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
        dialog.show();
        Toast.makeText(this, "Ovo je Dialog", Toast.LENGTH_SHORT).show();
    }
// Ovo nam pokazuje notifikacuju (obavestenja u vezi sa propistenim pozivima, phone update, etc.)
// u obliku torte na levom gornjem uglu ekrana.
    //Samu ikonicu ubacujemo u Drawable (is_cake_black_24dp)
    private void showNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIF_CHANNEL_ID);
        //Uvek obvezno da se stavi!
        builder.setContentTitle("Notifikacija")
                .setContentText("Ovo je tekst notifikacije")
                .setSmallIcon(R.drawable.ic_cake_black_24dp);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIF_ID, builder.build());
        Toast.makeText(this, "Ovo je Notifikacija", Toast.LENGTH_SHORT).show();
    }
//Ova metoda nam pokazuje poslednje polje u Draweru-u Preferences.
    private void showPrefs(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        PrefsFragment fragment = new PrefsFragment();
        //root u activity_main.xml pripada drawer_layoutu, a to znaci da kada otvorimo Preferences, da sam koren ekrana
        // bude u mogucnosti da otvori novi fragment na istom ekranu i potom izvrsimo unos/ispis podataka. Bez roota
        // ne bismo bili u mogucnosti da vidimo sadrzaj samih podesavanja (Preferences)
        transaction.replace(R.id.root, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, "Nas Notif Kanal", importance);
//            channel.setDescription("Opis naseg kanala");
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{250, 500, 250, 500, 250, 500});
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

// Fab nam je u ovom slucaju nacrtan crveni krug sa olovkom, koji se nalazi na donjem desnom uglu aplikacije.
    //Fab se prvo kreira u activity-main.xml, a onda ovde pozivamo tu metodu.
    private void setupFab(){
        //Fab pripada root-u. To obvezno
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar();
            }
        });
    }
// Ovo nam je probni fragment koji nam se nalazi na celom ekranu, kada kliknemo na drawerse Toast, Notification, Dialog...
    private void showProbaFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ProbaFragment fragmentPrimer = new ProbaFragment();
        fragmentPrimer.setContent(":)");
        transaction.replace(R.id.root, fragmentPrimer);
        transaction.commit();
    }



    // onOptionsItemSelected method is called whenever an item in the Toolbar is selected.

    //Ova metoda podrazumeva Izvrsavanje akcija. Ono se nalazi na vrhu app-a pored Toolbara sa desne strane.
    //Prvo u xml-u (menu.xml) odrediti metode izvrzavanja, a ovde metode pozivanja.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create:
                Toast.makeText(this, "Action create executed.", Toast.LENGTH_SHORT).show();
                //showPrefs();
                break;
            case R.id.action_update:
                Toast.makeText(this, "Action update executed.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_delete:
                Toast.makeText(this, "Action delete executed.", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
//Ova poslednja metoda ide zajedno za Izvrsavanje akcija. Bez ove metode, nase akcije ne bi bile vidljive!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}