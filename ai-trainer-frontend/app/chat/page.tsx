'use client';

import { useEffect, useState, useRef } from 'react';
import { apiFetch } from '@/lib/api';
import ReactMarkdown from 'react-markdown';

export default function Chat() {
  const [messages, setMessages] = useState<any[]>([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    fetchHistory();
  }, []);

  useEffect(() => {
    // Scroll to bottom when new messages arrive
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const fetchHistory = async () => {
    try {
      const data = await apiFetch('/chat/history');
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
      const res = await apiFetch('/chat', {
        method: 'POST',
        body: JSON.stringify({ message: userMsg.content })
      });
      
      const aiMsg = { role: 'assistant', content: res.reply, timestamp: res.timestamp };
      setMessages(prev => [...prev, aiMsg]);
    } catch (err: any) {
      console.error(err);
      // Remove the optimistic message if failed
      setMessages(prev => prev.filter(m => m !== userMsg));
      toast.error('The AI coach is temporarily unavailable. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const clearChat = async () => {
    if (!confirm('Are you sure you want to clear the conversation history?')) return;
    try {
      await apiFetch('/chat/clear', { method: 'DELETE' });
      setMessages([]);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="container section" style={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 80px)', paddingBottom: '24px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 className="heading-2">Your AI Coach</h1>
          <p className="text-muted text-sm">Ask about your diet, workouts, or just stay motivated.</p>
        </div>
        {messages.length > 0 && (
          <button onClick={clearChat} className="btn btn-ghost btn-sm text-muted">Clear History</button>
        )}
      </div>

      <div className="card" style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden', padding: 0 }}>
        
        {/* Chat Messages Area */}
        <div style={{ flex: 1, overflowY: 'auto', padding: '24px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          {messages.length === 0 ? (
            <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)' }}>
              Send a message to start chatting with your coach.
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
                      <p key={i} style={{ marginBottom: line ? '8px' : 0 }}>
                        {line}
                      </p>
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
