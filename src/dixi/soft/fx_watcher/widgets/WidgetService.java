package dixi.soft.fx_watcher.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return (new FxViewsFactory(this.getApplicationContext(), intent));
	}
}