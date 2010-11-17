package core;

import java.io.File;

public abstract class FileWatcher implements Runnable {
	
	public static final int AUTORELOAD_PERIODE = 1000;
	
	private long    m_timeStamp;
	private File    m_file;

	private Thread  m_thread;
	private boolean m_run;

	public FileWatcher(File file) {
		setFile(file);
	}
	
	public FileWatcher() {
	}
	
	public void setFile(File file) {
		this.m_file = file;
		this.m_timeStamp = file.lastModified();
	}
	
	public synchronized void start() {
		if (m_thread == null) {
			m_run = true;
			m_thread = new Thread(this);
			m_thread.start();
		}
	}
	
	public synchronized void stop() {
		m_run = false;
		if (m_thread == null) {
			try {
				m_thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				m_thread = null;
			}
		}
	}

	@Override
	public final void run() {				
		while (m_run) {	
			long timeStamp = m_file.lastModified();
			
			if (this.m_timeStamp != timeStamp) {
				this.m_timeStamp = timeStamp;
				onChange(m_file);
			}
			
			//sleep
			try {
				Thread.sleep(AUTORELOAD_PERIODE);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
				break;
			}
		}
	}

	protected abstract void onChange(File file);
}
