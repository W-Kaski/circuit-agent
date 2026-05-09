import React, { useState, useEffect, useRef } from 'react';
import Sidebar from './components/Sidebar';
import { chatStream } from './lib/api';
import {
  Send, ArrowLeft, Library, Globe, Loader2, Sparkles,
  FileText, ChevronRight
} from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

function WaveLogo({ size = 20, className = "" }) {
  return (
    <svg 
      width={size} 
      height={size} 
      viewBox="0 0 24 24" 
      fill="none" 
      xmlns="http://www.w3.org/2000/svg"
      className={className}
    >
      <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
      <path d="M6 12C8 12 10 7 12 12C14 17 16 12 18 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  );
}

function App() {
  const [query, setQuery] = useState('');
  const [messages, setMessages] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [sessionId] = useState(`session-${Date.now()}`);
  const [resultsActive, setResultsActive] = useState(false);
  const [mode, setMode] = useState('local');
  const [demoMode, setDemoMode] = useState(false);
  const textareaRef = useRef(null);
  const answerRef = useRef(null);

  // Auto-detect backend connectivity
  useEffect(() => {
    const apiBase = import.meta.env.VITE_API_BASE_URL || 'http://localhost:48124/api';
    fetch(`${apiBase}/knowledge/files`)
      .then(res => {
        if (!res.ok) throw new Error();
        setDemoMode(false);
        console.log("Backend detected. Live mode active.");
      })
      .catch(() => {
        setDemoMode(true);
        console.warn("Backend not reachable. Entering Demo Mode.");
      });
  }, []);

  const LOCAL_SUGGESTIONS = [
    "What is the KMP algorithm?",
    "Properties of Red-Black Trees",
    "How does the RAG system work?",
    "Explain Bloom Filters in detail"
  ];

  const WEB_SUGGESTIONS = [
    "Search for latest AI news",
    "What's the price of Bitcoin today?",
    "Weather forecast for San Francisco",
    "Find top research papers on LLMs"
  ];

  const MOCK_ANSWERS = {
    "What is the KMP algorithm?": "The Knuth-Morris-Pratt (KMP) algorithm is a highly efficient string-matching algorithm that searches for occurrences of a 'pattern' within a 'text'. Unlike the naive approach, KMP uses information about previous matches to skip unnecessary comparisons. It achieves a time complexity of $O(n+m)$, where $n$ is the text length and $m$ is the pattern length. It accomplishes this by pre-computing a 'failure function' (Next array) that dictates where to resume matching after a mismatch occurs.",
    "Properties of Red-Black Trees": "A Red-Black Tree is a specialized type of self-balancing binary search tree. Each node stores an extra bit representing 'color' (Red or Black), which is used to ensure the tree remains approximately balanced during insertions and deletions. The five defining properties are:\n1. Every node is either **Red** or **Black**.\n2. The **Root** node is always Black.\n3. Every **Leaf (NIL)** is Black.\n4. If a node is **Red**, then both its children must be **Black** (no two reds in a row).\n5. For each node, all simple paths from the node to descendant leaves contain the same number of **Black** nodes.",
    "How does the RAG system work?": "Retrieval-Augmented Generation (RAG) is a framework that combines the creative power of Large Language Models (LLMs) with the factual accuracy of external knowledge bases. The process involves:\n- **Ingestion**: Documents are split into chunks, converted into vector embeddings, and stored in a vector database.\n- **Retrieval**: When a user asks a question, the system searches the vector database for the most relevant chunks.\n- **Augmentation**: These chunks are prepended to the user's prompt as context.\n- **Generation**: The LLM generates a grounded response based on the provided context.",
    "Explain Bloom Filters in detail": "A Bloom Filter is a space-efficient probabilistic data structure used to test whether an element is a member of a set. It is incredibly fast and memory-efficient but has two trade-offs:\n- **Probabilistic Results**: It can return 'False Positives' (it says an element is in the set when it's not) but never 'False Negatives' (if it says it's not there, it's 100% not there).\n- **No Deletions**: Once an element is added, it's difficult to remove without rebuilding the filter.\nIt works by using multiple independent hash functions to map an element to several bits in a bit array. To check membership, it hashes the element and checks if all corresponding bits are set to 1.",
    "Search for latest AI news": "According to recent web crawls, the AI landscape is evolving rapidly:\n- **Agentic Workflows**: A shift from simple chat to autonomous agents (like Devin or Manus) that can plan and execute complex tasks.\n- **Multi-modal Models**: Models like GPT-4o and Gemini 1.5 Pro are now natively processing video, audio, and text simultaneously.\n- **Efficiency**: Research into Smaller Language Models (SLMs) is peaking, with Microsoft's Phi and Google's Gemma showing GPT-3.5 level performance on edge devices.\n- **Regulation**: Global governments are finalizing AI Safety Acts to govern large-scale deployments.",
    "What's the price of Bitcoin today?": "[DEMO MODE] In a live environment, I would call a financial API. Currently, Bitcoin is experiencing high volatility, trading around $65,000 - $70,000 range. For real-time data, please ensure the backend is connected to allow the agent to fetch live ticker information.",
    "Weather forecast for San Francisco": "[DEMO MODE] In live mode, I would use the WeatherTool to fetch real-time data. Typically, San Francisco weather is mild with temperatures between 15°C and 22°C. Current conditions are likely foggy (typical 'Karl the Fog') with a light breeze from the Pacific.",
    "Find top research papers on LLMs": "Top recent research papers include:\n1. **Attention Is All You Need**: The foundational Transformer paper.\n2. **Training Language Models to Follow Instructions**: The basis for InstructGPT.\n3. **Scaling Laws for Neural Language Models**: Insights into how model size and data affect performance.\n4. **Chain-of-Thought Prompting Elicits Reasoning in LLMs**: Explaining how multi-step reasoning improves AI accuracy."
  };

  // Auto-resize textarea
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = textareaRef.current.scrollHeight + 'px';
    }
  }, [query]);

  // Auto-scroll to bottom of chat
  useEffect(() => {
    if (answerRef.current) {
      answerRef.current.scrollTop = answerRef.current.scrollHeight;
    }
  }, [messages, isSearching]);

  const handleSearch = async (e, queryOverride) => {
    if (e) e.preventDefault();
    const currentQuery = queryOverride !== undefined ? queryOverride : query;
    if (!currentQuery.trim() || isSearching) return;

    setQuery(''); // Clear input box
    setIsSearching(true);
    setResultsActive(true);

    setMessages((prev) => [
      ...prev,
      { role: 'user', content: currentQuery },
      { role: 'ai', content: '', isStreaming: true }
    ]);

    if (demoMode) {
      // Simulate streaming for demo mode
      setTimeout(() => {
        const fullText = MOCK_ANSWERS[currentQuery] || "This is a demo environment. In Demo Mode, I only respond to pre-set questions. Try clicking the suggested questions below or ensure the backend is running to use Live Mode.";
        let currentPos = 0;
        const interval = setInterval(() => {
          if (currentPos < fullText.length) {
            const chunk = fullText.slice(currentPos, currentPos + 10);
            setMessages((prev) => {
              if (prev.length === 0) return prev;
              const newArr = [...prev];
              const lastIdx = newArr.length - 1;
              newArr[lastIdx] = { ...newArr[lastIdx], content: newArr[lastIdx].content + chunk };
              return newArr;
            });
            currentPos += 10;
          } else {
            clearInterval(interval);
            setIsSearching(false);
            setMessages((prev) => {
              const newArr = [...prev];
              const lastIdx = newArr.length - 1;
              if (lastIdx >= 0) {
                newArr[lastIdx] = { ...newArr[lastIdx], isStreaming: false };
              }
              return newArr;
            });
          }
        }, 20);
      }, 500);
      return;
    }

    const endpoint = mode === 'local' ? `/ai/chat/algorithm` : `/ai/chat/manus`;
    try {
      const sse = chatStream(endpoint, currentQuery, sessionId);

      sse.onopen = () => {
        console.log('SSE connection opened');
      };

      sse.onmessage = (event) => {
        if (event.data === '[DONE]') {
          setIsSearching(false);
          setMessages((prev) => {
            const newArr = [...prev];
            const lastIdx = newArr.length - 1;
            if (lastIdx >= 0) {
              newArr[lastIdx] = { ...newArr[lastIdx], isStreaming: false };
            }
            return newArr;
          });
          sse.close();
        } else {
          try {
            const parsed = JSON.parse(event.data);
            const chunkText = parsed.text !== undefined ? parsed.text : event.data;
            setMessages((prev) => {
              if (prev.length === 0) return prev;
              const newArr = [...prev];
              const lastIdx = newArr.length - 1;
              // Deep copy the last message object to avoid mutation bugs (especially in Strict Mode)
              newArr[lastIdx] = { 
                ...newArr[lastIdx], 
                content: newArr[lastIdx].content + chunkText 
              };
              return newArr;
            });
          } catch (e) {
            setMessages((prev) => {
              if (prev.length === 0) return prev;
              const newArr = [...prev];
              const lastIdx = newArr.length - 1;
              newArr[lastIdx] = { 
                ...newArr[lastIdx], 
                content: newArr[lastIdx].content + event.data 
              };
              return newArr;
            });
          }
        }
      };

      sse.onerror = (err) => {
        console.error('SSE error:', err);
        setIsSearching(false);
        setMessages((prev) => {
          const newArr = [...prev];
          const lastIdx = newArr.length - 1;
          if (lastIdx >= 0) {
            newArr[lastIdx] = { ...newArr[lastIdx], isStreaming: false };
          }
          return newArr;
        });
        sse.close();
      };
    } catch (err) {
      console.error('Failed to start chat:', err);
      setIsSearching(false);
    }
  };

  const resetSearch = () => {
    setResultsActive(false);
    setMessages([]);
    setQuery('');
    setIsSearching(false);
  };

  return (
    <div className="flex h-screen bg-[#1a1a1a] text-[#e0e0e0] font-sans overflow-hidden">
      <Sidebar demoMode={demoMode} />

      <main className="flex-1 flex flex-col min-w-0 relative">

        {/* ─── Landing / Empty state ─── */}
        {!resultsActive && (
          <div className="flex-1 flex flex-col items-center justify-center px-6">
            <div className="w-full max-w-[700px]">
              <div className="mb-8 text-center">
                <div className="w-10 h-10 bg-[#2a2a2a] rounded-xl flex items-center justify-center mx-auto mb-4 border border-[#333]">
                  <WaveLogo size={20} className="text-[#d4a373]" />
                </div>
                <h1 className="text-2xl font-semibold text-white tracking-tight mb-1">Circuit Agent</h1>
                <p className="text-[#888] text-sm">RAG-powered algorithm knowledge base</p>
              </div>

              <InputBox
                query={query}
                setQuery={setQuery}
                mode={mode}
                setMode={setMode}
                isSearching={isSearching}
                onSubmit={handleSearch}
                textareaRef={textareaRef}
                placeholder="Ask me about algorithms, data structures..."
              />

              {/* Suggested Questions */}
              <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 gap-3">
                {(mode === 'local' ? LOCAL_SUGGESTIONS : WEB_SUGGESTIONS).map((s, i) => (
                  <button
                    key={i}
                  onClick={() => handleSearch(null, s)}
                    className="text-left p-4 bg-[#212121] border border-[#333] rounded-xl hover:border-[#444] hover:bg-[#262626] transition-all group"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-sm text-[#aaa] group-hover:text-white transition-colors">{s}</span>
                      <ChevronRight size={14} className="text-[#444] group-hover:text-[#666]" />
                    </div>
                  </button>
                ))}
              </div>

              {/* Status Indicator (Only show if Demo Mode is active) */}
              {demoMode && (
                <div className="mt-10 flex justify-center">
                  <div className="flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#d4a373]/10 border border-[#d4a373]/20 text-[10px] font-medium text-[#d4a373] uppercase tracking-widest">
                    <div className="w-1.5 h-1.5 rounded-full bg-[#d4a373] animate-pulse" />
                    Demo Mode
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* ─── Results view ─── */}
        {resultsActive && (
          <div className="flex-1 flex flex-col min-h-0">
            {/* top bar */}
            <div className="px-8 pt-5 pb-0 flex items-center gap-3 shrink-0">
              <button
                onClick={resetSearch}
                className="flex items-center gap-1.5 text-[#888] hover:text-white transition-colors text-xs"
              >
                <ArrowLeft size={14} />
                New chat
              </button>
            </div>

            {/* scrollable answer area */}
            <div className="flex-1 overflow-y-auto px-8 py-6" ref={answerRef}>
              <div className="max-w-[700px] mx-auto pb-8 flex flex-col justify-end min-h-full">
                <div className="flex flex-col justify-end mt-auto">
                  {messages.map((msg, index) => (
                    <div key={index} className="mb-6">
                      {msg.role === 'user' ? (
                        <div className="flex justify-end">
                          <div className="max-w-[85%] bg-[#2f2f2f] border border-[#3a3a3a] rounded-2xl rounded-br-sm px-4 py-3">
                            <p className="text-[14px] text-[#e0e0e0] leading-relaxed">{msg.content}</p>
                          </div>
                        </div>
                      ) : (
                        <div className="flex items-start gap-3">
                          <div className="w-7 h-7 rounded-full bg-[#d4a373]/20 border border-[#d4a373]/30 flex items-center justify-center shrink-0 mt-0.5">
                            <WaveLogo size={14} className="text-[#d4a373]" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="text-[15px] text-[#e0e0e0] leading-[1.75] markdown-container">
                              <ReactMarkdown remarkPlugins={[remarkGfm]}>
                                {msg.content}
                              </ReactMarkdown>
                              {msg.isStreaming && (
                                <span className="inline-block w-0.5 h-4 bg-[#d4a373] ml-1 animate-pulse align-middle" />
                              )}
                              {!msg.content && msg.isStreaming && (
                                <span className="text-[#555] italic">Thinking...</span>
                              )}
                            </div>
                          </div>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Fixed bottom input */}
            <div className="shrink-0 px-8 pb-6 pt-2 border-t border-[#222] bg-[#1a1a1a]">
              <div className="max-w-[700px] mx-auto">
                <InputBox
                  query={query}
                  setQuery={setQuery}
                  mode={mode}
                  setMode={setMode}
                  isSearching={isSearching}
                  onSubmit={handleSearch}
                  textareaRef={textareaRef}
                  placeholder="Ask a follow-up..."
                  compact
                />
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

function InputBox({ query, setQuery, mode, setMode, isSearching, onSubmit, textareaRef, placeholder, compact }) {
  return (
    <form
      onSubmit={onSubmit}
      className={`bg-[#212121] border border-[#333] rounded-2xl focus-within:border-[#444] transition-colors ${compact ? 'pt-3 px-4 pb-3' : 'pt-4 px-5 pb-4'}`}
    >
      <textarea
        ref={textareaRef}
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder={placeholder}
        rows={1}
        className="w-full bg-transparent border-none text-[15px] text-white leading-relaxed resize-none outline-none placeholder:text-[#555] mb-3 max-h-[200px] overflow-y-auto"
        onKeyDown={(e) => {
          if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            onSubmit();
          }
        }}
      />

      <div className="flex items-center justify-between">
        {/* Mode toggle */}
        <div className="flex items-center gap-0.5 bg-[#1a1a1a] rounded-lg p-0.5 border border-[#2a2a2a]">
          <ModeBtn active={mode === 'local'} onClick={() => setMode('local')} icon={<Library size={11} />} label="Local" />
          <ModeBtn active={mode === 'web'}   onClick={() => setMode('web')}   icon={<Globe size={11} />}   label="Web" />
        </div>

        {/* Send button */}
        <button
          type="submit"
          disabled={isSearching || !query.trim()}
          className="w-8 h-8 bg-[#d4a373] hover:bg-[#c48d5e] disabled:opacity-30 disabled:cursor-not-allowed text-[#1a1a1a] rounded-lg flex items-center justify-center transition-all active:scale-95"
        >
          {isSearching ? <Loader2 size={14} className="animate-spin" /> : <Send size={14} />}
        </button>
      </div>
    </form>
  );
}

function ModeBtn({ active, onClick, icon, label }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex items-center gap-1 px-2.5 py-1 rounded-md text-[11px] font-medium transition-all ${
        active ? 'bg-[#2f2f2f] text-white' : 'text-[#555] hover:text-[#888]'
      }`}
    >
      {icon}
      {label}
    </button>
  );
}

export default App;
