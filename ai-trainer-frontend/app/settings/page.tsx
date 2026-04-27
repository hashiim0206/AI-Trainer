'use client';

import { useState } from 'react';
import { apiFetch, removeToken } from '@/lib/api';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function Settings() {
  const router = useRouter();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleDeleteAccount = async () => {
    const confirmDelete = window.confirm(
      "Are you absolutely sure you want to delete your account? This action cannot be undone and will permanently delete your profile, progress, and plans."
    );
    
    if (!confirmDelete) return;

    setLoading(true);
    setError('');

    try {
      await apiFetch('/account/delete', { method: 'DELETE' });
      removeToken();
      toast.success('Account successfully deleted.');
      router.push('/');
    } catch (err: any) {
      setError(err.message || 'Failed to delete account.');
      toast.error('Failed to delete account.');
      setLoading(false);
    }
  };

  return (
    <div className="container section">
      <h1 className="heading-1" style={{ marginBottom: '32px' }}>Settings</h1>

      {error && <div className="alert alert-error" style={{ marginBottom: '24px' }}>{error}</div>}

      <div className="card anim-fade-up">
        <h2 className="heading-2" style={{ marginBottom: '16px', color: 'var(--red)' }}>Danger Zone</h2>
        <p className="text-muted" style={{ marginBottom: '24px' }}>
          Deleting your account is permanent. All your data, including your profile, generated plans, AI chat history, and progress logs will be completely erased.
        </p>

        <button 
          onClick={handleDeleteAccount} 
          className="btn btn-danger"
          disabled={loading}
        >
          {loading ? <span className="spinner"></span> : 'Delete My Account'}
        </button>
      </div>
    </div>
  );
}
