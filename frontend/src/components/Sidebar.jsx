import React, { useState, useEffect } from 'react';
import { getFiles, uploadFile, deleteFile, api } from '../lib/api';
import { Plus, Trash2, FileText, RefreshCw, Layers, Database, X } from 'lucide-react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

export default function Sidebar({ demoMode = false }) {
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [syncing, setSyncing] = useState(false);
  const [error, setError] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewContent, setPreviewContent] = useState('');
  const fileInputRef = React.useRef(null);

  const fetchFiles = async () => {
    if (demoMode) {
      setFiles([
        { name: 'StringMatching.md', indexed: true, source: 'system' },
        { name: 'TreeStructures.md', indexed: true, source: 'system' },
        { name: 'ProbabilisticDS.md', indexed: true, source: 'system' }
      ]);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const res = await getFiles();
      setFiles(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error('Library fetch failed:', err);
      setError('Cannot connect to backend');
      setFiles([]);
    } finally {
      setLoading(false);
    }
  };

  const onSync = async () => {
    setSyncing(true);
    try {
      await api.post('/knowledge/reindex');
      await fetchFiles();
    } catch (err) {
      console.error('Re-index failed:', err);
    } finally {
      setSyncing(false);
    }
  };

  const onUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setLoading(true);
    try {
      await uploadFile(file);
      await fetchFiles();
    } catch (err) {
      console.error('Upload failed:', err);
    } finally {
      setLoading(false);
      e.target.value = '';
    }
  };

  const onDelete = async (name, e) => {
    e.stopPropagation();
    setLoading(true);
    try {
      await deleteFile(name);
      await fetchFiles();
      if (selectedFile === name) setSelectedFile(null);
    } catch (err) {
      console.error('Delete failed:', err);
    } finally {
      setLoading(false);
    }
  };

  const onPreview = async (name) => {
    setSelectedFile(name);
    setPreviewContent('Loading...');
    if (demoMode) {
      const mockDocs = {
        'StringMatching.md': "# String Matching Algorithms\n\n## KMP Algorithm\nThe Knuth-Morris-Pratt (KMP) algorithm is a string-matching algorithm that achieves $O(n+m)$ time complexity...",
        'TreeStructures.md': "# Tree Data Structures\n\n## Red-Black Trees\nA Red-Black Tree is a self-balancing binary search tree. It ensures $O(\\log n)$ for search, insert, and delete...",
        'ProbabilisticDS.md': "# Probabilistic Data Structures\n\n## Bloom Filters\nA Bloom Filter is a space-efficient data structure for set membership testing. It uses multiple hash functions..."
      };
      setPreviewContent(mockDocs[name] || "Document content not available in demo mode.");
      return;
    }

    try {
      const res = await api.get(`/knowledge/files/${name}/content`);
      setPreviewContent(res.data);
    } catch (err) {
      setPreviewContent('Failed to load content.');
    }
  };

  useEffect(() => {
    fetchFiles();
  }, [demoMode]);

  return (
    <aside className="w-56 bg-[#161616] border-r border-[#252525] flex flex-col shrink-0 h-full select-none">
      {/* Header */}
      <div className="px-4 pt-5 pb-4">
        <div className="flex items-center gap-2 mb-5">
          <Layers size={14} className="text-[#d4a373]" />
          <span className="text-[11px] font-semibold text-[#666] uppercase tracking-[0.15em]">Workspace</span>
        </div>

        <button
          onClick={() => fileInputRef.current?.click()}
          className="w-full flex items-center gap-2 px-3 py-2 rounded-lg bg-[#1e1e1e] hover:bg-[#252525] border border-[#2a2a2a] text-[12px] text-[#aaa] hover:text-white transition-colors"
        >
          <Plus size={13} />
          Add document
        </button>
        <input
          type="file"
          ref={fileInputRef}
          className="hidden"
          accept=".md,.txt"
          onChange={onUpload}
        />
      </div>

      {/* Library list */}
      <div className="flex-1 overflow-y-auto px-2">
        <p className="px-2 mb-2 text-[10px] font-semibold text-[#444] uppercase tracking-[0.15em]">Library</p>

        {error && (
          <div className="px-2 py-2 text-[11px] text-red-400/70">{error}</div>
        )}

        {loading && files.length === 0 && !error && (
          <div className="flex items-center justify-center py-8 text-[#333]">
            <RefreshCw size={13} className="animate-spin" />
          </div>
        )}

        {!loading && !error && files.length === 0 && (
          <div className="px-2 py-4 text-[11px] text-[#444] text-center">
            No documents yet
          </div>
        )}

        <div className="space-y-0.5">
          {files.map((file) => (
            <div
              key={file.name}
              onClick={() => onPreview(file.name)}
              className="group flex items-center justify-between px-2 py-2 rounded-lg hover:bg-[#1e1e1e] transition-colors cursor-pointer"
            >
              <div className="flex items-center gap-2 min-w-0">
                <FileText size={12} className="text-[#444] shrink-0" />
                <span className="text-[12px] text-[#888] group-hover:text-[#bbb] truncate">
                  {file.name.replace('.md', '')}
                </span>
                {file.indexed && (
                  <div className="w-1 h-1 rounded-full bg-[#4caf50] shrink-0" />
                )}
              </div>
              {file.source === 'user' && (
                <button
                  onClick={(e) => onDelete(file.name, e)}
                  className="opacity-0 group-hover:opacity-100 text-[#444] hover:text-red-400 transition-all p-0.5"
                >
                  <Trash2 size={11} />
                </button>
              )}
            </div>
          ))}
        </div>
      </div>

      {/* Sync button */}
      <div className="px-4 pb-4 pt-3 border-t border-[#1e1e1e]">
        <button
          onClick={onSync}
          disabled={syncing}
          className="w-full flex items-center gap-2 px-3 py-2 rounded-lg text-[11px] text-[#555] hover:text-[#888] hover:bg-[#1e1e1e] transition-colors"
        >
          <RefreshCw size={11} className={syncing ? 'animate-spin text-[#d4a373]' : ''} />
          <span>{syncing ? 'Syncing...' : 'Sync database'}</span>
          <Database size={11} className="ml-auto opacity-50" />
        </button>
      </div>

      {/* Preview Modal */}
      {selectedFile && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-[#1a1a1a] border border-[#333] rounded-2xl w-full max-w-3xl max-h-[85vh] flex flex-col shadow-2xl">
            <div className="flex items-center justify-between px-6 py-4 border-b border-[#2a2a2a]">
              <div className="flex items-center gap-2">
                <FileText size={16} className="text-[#d4a373]" />
                <h3 className="text-[14px] font-medium text-white">{selectedFile}</h3>
              </div>
              <button 
                onClick={() => setSelectedFile(null)}
                className="text-[#666] hover:text-white p-1 rounded-md hover:bg-[#2a2a2a] transition-colors"
              >
                <X size={18} />
              </button>
            </div>
            <div className="p-6 overflow-y-auto custom-scrollbar">
              <div className="markdown-container text-[14px] text-[#ccc] leading-[1.75]">
                <ReactMarkdown remarkPlugins={[remarkGfm]}>
                  {previewContent}
                </ReactMarkdown>
              </div>
            </div>
          </div>
        </div>
      )}
    </aside>
  );
}
