package eecs481.homework.supertictactoe;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BoardView bv = (BoardView) this.findViewById(R.id.boardview);
        
        Button button = (Button) this.findViewById(R.id.restartB);
        button.setOnClickListener( new OnClickListener() {
        	@Override
        	public void onClick(View v) {
        		bv.reset();
        	}
        });
        
        bv.reset();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
