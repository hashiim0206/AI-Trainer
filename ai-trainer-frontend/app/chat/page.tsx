'use client';

import { useEffect, useState, useRef } from 'react';
import { apiFetch } from '@/lib/api';
import ReactMarkdown from 'react-markdown';
import toast from 'react-hot-toast';

export default function Chat() {
  const [sessions, setSessions] = useState<any[]>([]);
  const [currentSessionId, setCurrentSessionId] = useState<number | null>(null);
  const [messages, setMessages] = useState<any[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchSessions();
  }, []);

  useEffect(() => {
    if (currentSessionId) {
      fetchHistory(currentSessionId);
    } else {
      setMessages([]);
    }
  }, [currentSessionId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const fetchSessions = async () => {
    try {
      const data = await apiFetch('/chat/sessions');
      setSessions(data);
      if (data.length > 0 && !currentSessionId) {
        setCurrentSessionId(data[0].id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchHistory = async (sessionId: number) => {
    try {
      const data = await apiFetch(`/chat/history/${sessionId}`);
      setMessages(data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMsg = { role: 'user', content: input, timestamp: new Date().toISOString() };
    setMessages(prev => [...prev, userMsg]);
    setInput('');
    setLoading(true);

    try {
      const payload: any = { message: userMsg.content };
      if (currentSessionId) payload.sessionId = currentSessionId;

      const res = await apiFetch('/chat', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      
      const aiMsg = { role: 'assistant', content: res.reply, timestamp: res.timestamp };
      setMessages(prev => [...prev, aiMsg]);
      
      // If it was a new session, update our state and refresh the sidebar
      if (!currentSessionId && res.sessionId) {
        setCurrentSessionId(res.sessionId);
        fetchSessions();
      }
    } catch (err: any) {
      console.error(err);
      setMessages(prev => prev.filter(m => m !== userMsg));
      toast.error('The AI coach is temporarily unavailable.');
    } finally {
      setLoading(false);
    }
  };

  const createNewChat = () => {
    setCurrentSessionId(null);
    setMessages([]);
  };

  const deleteSession = async (sessionId: number, e: React.MouseEvent) => {
    e.stopPropagation();
    if (!confirm('Are you sure you want to delete this chat session?')) return;
    
    try {
      await apiFetch(`/chat/session/${sessionId}`, { method: 'DELETE' });
      const updatedSessions = sessions.filter(s => s.id !== sessionId);
      setSessions(updatedSessions);
      if (currentSessionId === sessionId) {
        setCurrentSessionId(updatedSessions.length > 0 ? updatedSessions[0].id : null);
      }
      toast.success('Session deleted');
    } catch (err) {
      console.error(err);
      toast.error('Failed to delete session');
    }
  };

  return (
    <div className="container" style={{ display: 'flex', height: 'calc(100vh - 80px)', paddingTop: '24px', paddingBottom: '24px', gap: '24px' }}>
      
      {/* Sidebar for Sessions */}
      <div className="card" style={{ width: '280px', display: 'flex', flexDirection: 'column', padding: '16px' }}>
        <button onClick={createNewChat} className="btn btn-primary" style={{ marginBottom: '16px', justifyContent: 'center' }}>
          + New Chat
        </button>
        
        <h3 className="text-muted text-sm text-uppercase" style={{ marginBottom: '12px', fontWeight: 'bold', letterSpacing: '0.05em' }}>Recent Chats</h3>
        
        <div style={{ flex: 1, overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {sessions.length === 0 && (
            <p className="text-muted text-sm">No recent chats.</p>
          )}
          {sessions.map(session => (
            <div 
              key={session.id} 
              onClick={() => setCurrentSessionId(session.id)}
              style={{
                padding: '12px', borderRadius: '8px', cursor: 'pointer',
                background: currentSessionId === session.id ? 'var(--primary-glow)' : 'transparent',
                border: `1px solid ${currentSessionId === session.id ? 'var(--primary)' : 'transparent'}`,
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                transition: 'all 0.2s'
              }}
              className="hover-bg"
            >
              <span style={{ fontSize: '14px', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', flex: 1, color: currentSessionId === session.id ? 'var(--text)' : 'var(--text-2)' }}>
                {session.title}
              </span>
              <button 
                onClick={(e) => deleteSession(session.id, e)}
                style={{ background: 'none', border: 'none', color: 'var(--red)', cursor: 'pointer', padding: '4px', opacity: 0.6 }}
                title="Delete Chat"
              >
                ×
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="card" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden', padding: 0 }}>
        
        {/* Header */}
        <div style={{ padding: '16px 24px', borderBottom: '1px solid var(--border)', background: 'var(--bg-3)' }}>
          <h2 className="heading-3">{currentSessionId ? sessions.find(s => s.id === currentSessionId)?.title || 'Chat' : 'New Chat'}</h2>
        </div>

        {/* Chat Messages */}
        <div style={{ flex: 1, overflowY: 'auto', padding: '24px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {messages.length === 0 ? (
            <div style={{ height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}>
              <div style={{ fontSize: '48px', marginBottom: '16px' }}>🤖</div>
              <h3 className="heading-3">How can I help you today?</h3>
              <p className="text-muted text-sm mt-2">Ask about diet, form, or feeling sore.</p>
            </div>
          ) : (
            messages.map((msg, idx) => (
              <div key={idx} style={{ 
                display: 'flex', 
                flexDirection: msg.role === 'user' ? 'row-reverse' : 'row',
                gap: '12px'
              }}>
                <div style={{
                  width: '32px', height: '32px', borderRadius: '50%',
                  background: msg.role === 'user' ? 'var(--primary)' : 'var(--accent)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontWeight: 'bold', fontSize: '14px', color: '#fff', flexShrink: 0
                }}>
                  {msg.role === 'user' ? 'U' : 'AI'}
                </div>
                <div style={{
                  background: msg.role === 'user' ? 'var(--primary-glow)' : 'var(--bg-3)',
                  border: `1px solid ${msg.role === 'user' ? 'rgba(99,102,241,0.3)' : 'var(--border)'}`,
                  padding: '12px 16px',
                  borderRadius: '16px',
                  borderTopRightRadius: msg.role === 'user' ? '4px' : '16px',
                  borderTopLeftRadius: msg.role === 'assistant' ? '4px' : '16px',
                  maxWidth: '80%',
                  fontSize: '14px',
                  color: 'var(--text-1)'
                }} className={msg.role === 'assistant' ? 'markdown-content' : ''}>
                  {msg.role === 'assistant' ? (
                    <ReactMarkdown>{msg.content}</ReactMarkdown>
                  ) : (
                    msg.content.split('\n').map((line: string, i: number) => (
                      <p key={i} style={{ marginBottom: line ? '8px' : 0 }}>{line}</p>
                    ))
                  )}
                </div>
              </div>
            ))
          )}
          {loading && (
            <div style={{ display: 'flex', gap: '12px' }}>
              <div style={{
                  width: '32px', height: '32px', borderRadius: '50%',
                  background: 'var(--accent)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontWeight: 'bold', fontSize: '14px', color: '#fff'
                }}>AI</div>
              <div style={{ padding: '12px 16px', background: 'var(--bg-3)', borderRadius: '16px', borderTopLeftRadius: '4px' }}>
                <span className="spinner" style={{ width: '16px', height: '16px', borderWidth: '2px' }}></span>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div style={{ padding: '16px', borderTop: '1px solid var(--border)', background: 'rgba(0,0,0,0.2)' }}>
          <form onSubmit={handleSend} style={{ display: 'flex', gap: '12px' }}>
            <input 
              type="text" 
              className="form-input" 
              placeholder="Ask your coach anything..." 
              value={input}
              onChange={e => setInput(e.target.value)}
              disabled={loading}
              style={{ flex: 1, borderRadius: '99px' }}
            />
            <button type="submit" className="btn btn-primary" style={{ borderRadius: '99px' }} disabled={loading || !input.trim()}>
              Send
            </button>
          </form>
        </div>

      </div>
    </div>
  );
}
