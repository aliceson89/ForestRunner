package Engine.Core;

import java.nio.file.Paths;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

/**
 * <h1>Maintains a given sound, Music or Effect</h1>
 * <b>Creation Date:</b> June 21, 2016<br>
 * <b>Modified Date:</b> April 06, 2021<p>
 * @author Trevor Lane
 * @version 1.1
 */
public class SoundClip 
{
	String path = "";
	private Media fileURL = null;
	private MediaPlayer sound = null;
	JFXPanel dummy = new JFXPanel();
	
	private boolean interruptable = false;
	private boolean looping = false;
	
	/**
	 * <b><i>SoundClip</b></i><p>
	 * &nbsp&nbsp{@code public SoundClip(String path, boolean interruptable)}<p>
	 * Create a sound clip for future play, may be of .wav or .mp3 formats only
	 * @param path The relative path location of the sound file<br>e.g."/sounds/gunshot.wav" is in the res/sounds folder
	 * @param interruptable is true if the sound can be interrupted to be restarted
	 */
	public SoundClip(String path, boolean interruptable)
	{
		try
		{
			this.path = "resources" + path;
			fileURL = new Media(Paths.get(this.path).toUri().toString());
			sound = new MediaPlayer(fileURL);
			
			this.interruptable = interruptable;
			sound.setVolume(1.0);
		}
		catch(Exception e)
		{
			System.out.println("ERROR Loading file: " + this.path);
			e.printStackTrace();
		}
	}
	
	/**
	 * <b><i>Play</b></i><p>
	 * &nbsp&nbsp{@code public boolean Play()}<p>
	 * Attempts to play the currently loaded sound
	 * @return true if the sounds successfully began playing, false otherwise
	 */
	public boolean Play()
	{
		Status status = sound.getStatus();
		
		if (sound != null && status != Status.UNKNOWN && status != Status.HALTED &&
		   (status != MediaPlayer.Status.PLAYING) || interruptable)
		{
			if (looping == true)
			{
				sound.setCycleCount(MediaPlayer.INDEFINITE);
			}
			else
			{
				sound.setCycleCount(1);
			}
			
			//anything other than paused should reset the sound
			if (status != MediaPlayer.Status.PAUSED)
			{
				sound.seek(sound.getStartTime());
			}
			
			sound.play();
			
			sound.setOnEndOfMedia(new Runnable() 
			{
				public void run() 
				{
					sound.seek(sound.getStartTime());
					if (!looping)
					{
						Stop();
					}
				}
			});
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * <b><i>PlayLooping</b></i><p>
	 * &nbsp&nbsp{@code public void PlayLooping()}<p>
	 * Attempts to play the currently loaded sound that will<br>
	 * continue to play until manually stopped
	 */
	public void PlayLooping()
	{
		looping = true;
		Play();
	}
	
	/**
	 * <b><i>Stop</b></i><p>
	 * &nbsp&nbsp{@code public void Stop()}<p>
	 * Stop the currently loaded sound
	 */
	public void Stop()
	{
		Status status = sound.getStatus();
		
		if (sound != null && 
		   (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED))
		{
			looping = false;
			sound.setCycleCount(1);
			sound.stop();
		}
	}
	
	/**
	 * <b><i>Pause</b></i><p>
	 * &nbsp&nbsp{@code public void Pause()}<p>
	 * Pause the current playback of the sound, Play() or PlayLooping() will resume play from the pause point
	 */
	public void Pause()
	{
		if (sound != null && sound.getStatus() == MediaPlayer.Status.PLAYING)
		{
			sound.pause();
		}
	}
	
	/**
	 * <b><i>GetVolume</b></i><p>
	 * &nbsp&nbsp{@code public double GetVolume()}<p>
	 * Retrieve the volume level of the currently loaded sound
	 * @return The current volume level between 0.0 and 1.0<br>
	 * of the currently loaded sound
	 */
	public double GetVolume()
	{
		return sound.getVolume();
	}
	
	/**
	 * <b><i>SetVolume</b></i><p>
	 * &nbsp&nbsp{@code public void SetVolume(double level)}<p>
	 * Set the volume level of the loaded sound
	 * @param level The new volume level, clamped between 0.0 and 1.0
	 */
	public void SetVolume(double level)
	{
		//clamp between 0 and 1
		sound.setVolume(Math.max(0.0,Math.min(1.0, level)));
	}
	
	/**
	 * <b><i>GetLooping</b></i><p>
	 * &nbsp&nbsp{@code public boolean IsLooping()}<p>
	 * Retrieves the play looping status of the loaded sound
	 * @return true if the sound is set to loop, false otherwise
	 */
	public boolean IsLooping()
	{
		return looping;
	}
	
	/**
	 * <b><i>IsPlaying</b></i><p>
	 * &nbsp&nbsp{@code public boolean IsPlaying()}<p>
	 * Retrieve the active play status of the sound
	 * @return Returns true if the sound is currently playing, false otherwise
	 */
	public boolean IsPlaying()
	{
		if (sound != null && sound.getStatus() == MediaPlayer.Status.PLAYING)
		{
			return true;
		}
		
		//Not playing
		return false;
	}
}