import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import './Budget.css';

const CATEGORIES = ['FOOD', 'RENT', 'TRANSPORT', 'ENTERTAINMENT', 'SHOPPING', 'HEALTHCARE', 'EDUCATION', 'EMI', 'OTHER_EXPENSE'];

const Budget = () => {
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ category: 'FOOD', limitAmount: '' });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchBudgets();
  }, []);

  const fetchBudgets = async () => {
    try {
      const res = await API.get('/api/budgets');
      setBudgets(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await API.post('/api/budgets', {
        category: form.category,
        limitAmount: parseFloat(form.limitAmount),
      });
      setShowForm(false);
      setForm({ category: 'FOOD', limitAmount: '' });
      fetchBudgets();
    } catch (err) {
      setError('Failed to set budget');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this budget?')) return;
    try {
      await API.delete(`/api/budgets/${id}`);
      fetchBudgets();
    } catch (err) {
      alert('Failed to delete');
    }
  };

  const getProgressColor = (percentage) => {
    if (percentage >= 100) return '#e74c3c';
    if (percentage >= 80) return '#f39c12';
    return '#4ecca3';
  };

  return (
    <div className="budget-page">
      <div className="page-header">
        <div>
          <h2>Budget</h2>
          <p>Set and track your monthly spending limits</p>
        </div>
        <button className="add-btn" onClick={() => setShowForm(!showForm)}>
          {showForm ? '✕ Cancel' : '+ Set Budget'}
        </button>
      </div>

      {/* Set Budget Form */}
      {showForm && (
        <div className="form-card">
          <h3>Set Monthly Budget</h3>
          {error && <div className="error-msg">{error}</div>}
          <form onSubmit={handleSubmit} className="budget-form">
            <div className="form-row">
              <div className="form-group">
                <label>Category</label>
                <select
                  value={form.category}
                  onChange={(e) => setForm({ ...form, category: e.target.value })}
                >
                  {CATEGORIES.map(c => (
                    <option key={c} value={c}>{c.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Monthly Limit (₹)</label>
                <input
                  type="number"
                  placeholder="e.g. 5000"
                  value={form.limitAmount}
                  onChange={(e) => setForm({ ...form, limitAmount: e.target.value })}
                  required
                  min="1"
                />
              </div>
            </div>
            <button type="submit" className="submit-btn" disabled={submitting}>
              {submitting ? 'Saving...' : 'Save Budget'}
            </button>
          </form>
        </div>
      )}

      {/* Budget Cards */}
      {loading ? (
        <div className="loading">Loading budgets...</div>
      ) : budgets.length === 0 ? (
        <div className="no-data">No budgets set yet. Click "Set Budget" to start!</div>
      ) : (
        <div className="budget-grid">
          {budgets.map(budget => {
            const percentage = Math.min(budget.percentageUsed, 100);
            const color = getProgressColor(budget.percentageUsed);
            return (
              <div key={budget.id} className={`budget-card ${budget.isExceeded ? 'exceeded' : ''}`}>
                <div className="budget-card-header">
                  <span className="budget-category">{budget.category?.replace('_', ' ')}</span>
                  <button className="delete-btn" onClick={() => handleDelete(budget.id)}>🗑</button>
                </div>

                {/* Progress Bar */}
                <div className="progress-bar-container">
                  <div
                    className="progress-bar"
                    style={{ width: `${percentage}%`, background: color }}
                  />
                </div>

                <div className="budget-stats">
                  <div className="stat">
                    <span className="stat-label">Spent</span>
                    <span className="stat-value" style={{ color }}>
                      ₹{parseFloat(budget.spentAmount || 0).toLocaleString()}
                    </span>
                  </div>
                  <div className="stat">
                    <span className="stat-label">Limit</span>
                    <span className="stat-value">₹{parseFloat(budget.limitAmount).toLocaleString()}</span>
                  </div>
                  <div className="stat">
                    <span className="stat-label">Remaining</span>
                    <span className="stat-value" style={{ color: budget.isExceeded ? '#e74c3c' : '#27ae60' }}>
                      {budget.isExceeded ? '-' : ''}₹{Math.abs(parseFloat(budget.remainingAmount || 0)).toLocaleString()}
                    </span>
                  </div>
                </div>

                <div className="budget-percentage" style={{ color }}>
                  {budget.percentageUsed?.toFixed(1)}% used
                  {budget.isExceeded && <span className="exceeded-badge">EXCEEDED</span>}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Budget;