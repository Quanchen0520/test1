package hcvs.cs.myapplication

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
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
        val downloadBtn: ImageButton = itemView.findViewById(R.id.DownloadBtn)
        val seekBar: SeekBar = itemView.findViewById(R.id.seekBar)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(position: Int, musicItem: MusicItem) {
            itemView.findViewById<TextView>(R.id.MusicName).text = musicItem.SongName
            musicImage.load(musicItem.imageURL)
            setupUI(position)
            setupListeners(position, musicItem.SongURL)
        }

        private fun setupUI(position: Int) {
            playBtn.setImageResource(
                if (currentPlayerPosition == position) R.drawable.baseline_stop_circle_24
                else R.drawable.baseline_play_circle_24
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
                else url?.let{ playmusic(it, position, this)}
            }
        }
    }

    private fun playmusic(url: String, position: Int, holder: UesrViewHolder) {
        TODO("Not yet implemented")
    }

    private fun stopmusic() {
        TODO("Not yet implemented")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UesrViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UesrViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}