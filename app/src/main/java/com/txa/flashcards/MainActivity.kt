package com.txa.flashcards

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.txa.flashcards.ai.AiWordGenerator
import com.txa.flashcards.data.WordDatabase
import com.txa.flashcards.databinding.ActivityMainBinding
import com.txa.flashcards.repository.WordRepository
import com.txa.flashcards.utils.ApiKeyManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: WordRepository
    private lateinit var aiGenerator: AiWordGenerator
    private var words: List<com.txa.flashcards.data.Word> = emptyList()
    private var currentIndex = 0
    private var isFlipped = false
    private var isGenerating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        // Initialize database and repository
        val database = WordDatabase.getDatabase(this)
        repository = WordRepository(database.wordDao())
        
        // Initialize AI generator
        val apiKey = ApiKeyManager.getApiKey(this)
        aiGenerator = AiWordGenerator(apiKey)

        // Setup animations
        setupCardAnimations()

        // Load words (will auto-generate if empty)
        loadWords()

        // Setup click listeners
        binding.flashCard.setOnClickListener {
            flipCard()
        }

        binding.btnNext.setOnClickListener {
            nextCard()
        }

        binding.btnPrevious.setOnClickListener {
            previousCard()
        }

        binding.fabWordList.setOnClickListener {
            // Generate more words with AI
            generateMoreWords()
        }

        // Show copyright toast on first launch
        showCopyrightToast()
    }

    private fun setupCardAnimations() {
        val scale = resources.displayMetrics.density
        binding.flashCard.cameraDistance = 8000 * scale
    }

    private fun loadWords() {
        lifecycleScope.launch {
            repository.getAllWords().collect { wordList ->
                if (wordList.isEmpty() && !isGenerating) {
                    // Auto-generate words using AI if database is empty
                    generateWordsWithAi()
                } else if (wordList.isNotEmpty()) {
                    words = wordList
                    displayCurrentCard()
                }
            }
        }
    }
    
    private fun generateWordsWithAi() {
        if (isGenerating) return
        
        isGenerating = true
        binding.cardText.text = "Generating words with AI..."
        binding.cardCounter.text = "Loading..."
        
        lifecycleScope.launch {
            try {
                // Generate 30 words with mixed categories
                val generatedWords = aiGenerator.generateWords(
                    count = 30,
                    category = null,
                    difficulty = 1
                )
                
                if (generatedWords.isNotEmpty()) {
                    repository.insertWords(generatedWords)
                    Toast.makeText(
                        this@MainActivity,
                        "Generated ${generatedWords.size} words with AI!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Fallback to sample words if AI fails
                    Toast.makeText(
                        this@MainActivity,
                        "AI generation failed. Using sample words.",
                        Toast.LENGTH_LONG
                    ).show()
                    // You can add fallback sample words here if needed
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error generating words: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isGenerating = false
            }
        }
    }

    private fun displayCurrentCard() {
        if (words.isEmpty()) return

        val word = words[currentIndex]
        isFlipped = false

        binding.languageLabel.text = getString(R.string.english)
        binding.cardText.text = word.english
        binding.cardExample.text = word.example ?: ""
        binding.cardExample.visibility = if (word.example.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.cardCounter.text = "${currentIndex + 1} / ${words.size}"

        // Reset card rotation
        binding.flashCard.rotationY = 0f
    }

    private fun flipCard() {
        if (words.isEmpty()) return

        val word = words[currentIndex]
        
        // Animate flip
        binding.flashCard.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                // Change content at midpoint
                isFlipped = !isFlipped
                if (isFlipped) {
                    // Show Vietnamese
                    binding.languageLabel.text = getString(R.string.vietnamese)
                    binding.cardText.text = word.vietnamese
                    binding.cardExample.visibility = View.GONE
                } else {
                    // Show English
                    binding.languageLabel.text = getString(R.string.english)
                    binding.cardText.text = word.english
                    binding.cardExample.text = word.example ?: ""
                    binding.cardExample.visibility = if (word.example.isNullOrEmpty()) View.GONE else View.VISIBLE
                }
                // Complete flip
                binding.flashCard.rotationY = -90f
                binding.flashCard.animate()
                    .rotationY(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun nextCard() {
        if (words.isEmpty()) return
        currentIndex = (currentIndex + 1) % words.size
        isFlipped = false
        displayCurrentCard()
    }

    private fun previousCard() {
        if (words.isEmpty()) return
        currentIndex = if (currentIndex == 0) words.size - 1 else currentIndex - 1
        isFlipped = false
        displayCurrentCard()
    }

    private fun generateMoreWords() {
        if (isGenerating) {
            Toast.makeText(this, "Already generating...", Toast.LENGTH_SHORT).show()
            return
        }
        
        lifecycleScope.launch {
            try {
                isGenerating = true
                Toast.makeText(
                    this@MainActivity,
                    "Generating more words with AI...",
                    Toast.LENGTH_SHORT
                ).show()
                
                val generatedWords = aiGenerator.generateWords(
                    count = 20,
                    category = null,
                    difficulty = 1
                )
                
                if (generatedWords.isNotEmpty()) {
                    repository.insertWords(generatedWords)
                    Toast.makeText(
                        this@MainActivity,
                        "Added ${generatedWords.size} new words!",
                        Toast.LENGTH_SHORT
                    ).show()
                    showCopyrightToast()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to generate words. Check API key.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isGenerating = false
            }
        }
    }
    
    private fun showCopyrightToast() {
        Toast.makeText(this, getString(R.string.copyright), Toast.LENGTH_SHORT).show()
    }
}

