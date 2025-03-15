package hcvs.cs.myapplication

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class Adapter(
    private val musicList: List<MusicItem>,
    private val onDownloadClick: (Int) -> Unit
) : RecyclerView.Adapter<Adapter.UesrViewHolder>() {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayerPosition: Int? = null
    private val handler = Handler(Looper.getMainLooper())
    private val seekBarUpdateRunnable = HashMap<Int, Runnable>()
    private val downloadInProcess = mutableMapOf<Int, Boolean>()
    private val downloadProcessMap = mutableMapOf<Int, Int>()
    private val downloadCompleted = mutableMapOf<Int, Boolean>()

    inner class UesrViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val musicImage: ImageView = itemView.findViewById(R.id.MusicImage)
        val playBtn: ImageButton = itemView.findViewById(R.id.PlayBtn)
        private val downloadBtn: ImageButton = itemView.findViewById(R.id.DownloadBtn)
        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(position: Int, musicItem: MusicItem) {
            itemView.findViewById<TextView>(R.id.MusicName).text = musicItem.SongName
            musicImage.load(musicItem.imageURL)
            setupUI(position)
            setupListeners(position, musicItem.SongURL)
        }

        private fun setupUI(position: Int) {
            playBtn.setImageResource(
                try {
                    if (currentPlayerPosition == position) R.drawable.baseline_stop_circle_24
                    else R.drawable.baseline_play_circle_24
                } catch (e:Exception) {
                    Log.e("Adapter", "播放資源錯誤",e)
                }

            )
            seekBar.visibility =
                if (currentPlayerPosition == position) View.VISIBLE
                else View.GONE

            progressBar.visibility =
                if (downloadInProcess[position] == true) View.VISIBLE
                else View.GONE
            progressBar.progress = downloadProcessMap[position] ?: 0

            downloadBtn.setImageResource(
                if (downloadCompleted[position] == true) R.drawable.baseline_cloud_done_24
                else R.drawable.baseline_cloud_download_24
            )
            downloadBtn.isEnabled = downloadCompleted[position] != true
        }

        private fun setupListeners(position: Int, url: String?) {
            playBtn.setOnClickListener {
                if (currentPlayerPosition == position) stopmusic()
                else url?.let { playmusic(it, position, this) }
            }

            downloadBtn.setOnClickListener {
                if (downloadCompleted[position] != true) stardownload(position)
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    p0: SeekBar?,
                    p1: Int,
                    p2: Boolean
                ) {
                    if (p2) mediaPlayer?.seekTo(p1 * mediaPlayer!!.duration / 100)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }

        private fun stardownload(position: Int) {
            downloadInProcess[position] = true
            progressBar.visibility = View.VISIBLE
            progressBar.progress = 0
            onDownloadClick(position)
        }
    }

    private fun playmusic(url: String, position: Int, holder: UesrViewHolder) {
        stopmusic()
        currentPlayerPosition = position
        holder.playBtn.setImageResource(R.drawable.baseline_stop_circle_24)
        holder.seekBar.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                start()
                updateSeekBar(position, holder)
            }
            setOnCompletionListener { stopmusic() }
            prepareAsync()
        }
    }

    private fun updateSeekBar(position: Int, holder: UesrViewHolder) {
        val runnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        holder.seekBar.progress = (player.currentPosition * 100) / player.duration
                        handler.postDelayed(this, 500)
                    }
                }
            }
        }
        seekBarUpdateRunnable[position] = runnable
        handler.post(runnable)
    }

    private fun stopmusic() {
        currentPlayerPosition?.let {
            seekBarUpdateRunnable.remove(it)?.let { handler.removeCallbacks(it) }
            notifyItemChanged(it)
        }
        mediaPlayer?.release()
        mediaPlayer = null
        currentPlayerPosition = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UesrViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.musiclist, parent, false)
    )

    override fun getItemCount() = musicList.size

    override fun onBindViewHolder(holder: UesrViewHolder, position: Int) {
        try {
            musicList.getOrNull(position)?.let { holder.bind(position, it) }
        } catch (e: Exception) {
            Log.e("Adapter", "綁定 ViewHolder 時發生錯誤", e)
        }
    }

    fun updateDownloadProgress(position: Int, progress: Int) {
        downloadProcessMap[position] = progress
        if (progress >= 100) {
            downloadInProcess[position] = false
            downloadCompleted[position] = true
        }
        notifyItemChanged(position)
    }

    fun releaseResources() {
        try {
            stopmusic()
            handler.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            Log.e("Adapter", "釋放資源時發生錯誤", e)
        }
    }
}