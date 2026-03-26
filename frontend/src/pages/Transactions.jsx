import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import './Transactions.css';

const CATEGORIES_INCOME = ['SALARY', 'FREELANCE', 'INVESTMENT', 'OTHER_INCOME'];
const CATEGORIES_EXPENSE = ['FOOD', 'RENT', 'TRANSPORT', 'ENTERTAINMENT', 'SHOPPING', 'HEALTHCARE', 'EDUCATION', 'EMI', 'OTHER_EXPENSE'];

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    type: 'EXPENSE',
    amount: '',
    category: 'FOOD',
    description: '',
    transactionDate: new Date().toISOString().split('T')[0],
  });
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTransactions();
  }, []);

  const fetchTransactions = async () => {
    try {
      const res = await API.get('/api/transactions');
      setTransactions(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const updated = { ...form, [e.target.name]: e.target.value };
    // Reset category when type changes
    if (e.target.name === 'type') {
      updated.category = e.target.value === 'INCOME' ? 'SALARY' : 'FOOD';
    }
    setForm(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await API.post('/api/transactions', {
        ...form,
        amount: parseFloat(form.amount),
      });
      setShowForm(false);
      setForm({ type: 'EXPENSE', amount: '', category: 'FOOD', description: '', transactionDate: new Date().toISOString().split('T')[0] });
      fetchTransactions();
    } catch (err) {
      setError('Failed to add transaction');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this transaction?')) return;
    try {
      await API.delete(`/api/transactions/${id}`);
      fetchTransactions();
    } catch (err) {
      alert('Failed to delete transaction');
    }
  };

  const categories = form.type === 'INCOME' ? CATEGORIES_INCOME : CATEGORIES_EXPENSE;

  return (
    <div className="transactions-page">
      <div className="page-header">
        <div>
          <h2>Transactions</h2>
          <p>Manage your income and expenses</p>
        </div>
        <button className="add-btn" onClick={() => setShowForm(!showForm)}>
          {showForm ? '✕ Cancel' : '+ Add Transaction'}
        </button>
      </div>

      {/* Add Transaction Form */}
      {showForm && (
        <div className="form-card">
          <h3>New Transaction</h3>
          {error && <div className="error-msg">{error}</div>}
          <form onSubmit={handleSubmit} className="tx-form">
            <div className="form-row">
              <div className="form-group">
                <label>Type</label>
                <select name="type" value={form.type} onChange={handleChange}>
                  <option value="EXPENSE">Expense</option>
                  <option value="INCOME">Income</option>
                </select>
              </div>
              <div className="form-group">
                <label>Amount (₹)</label>
                <input
                  type="number"
                  name="amount"
                  placeholder="0.00"
                  value={form.amount}
                  onChange={handleChange}
                  required
                  min="0"
                  step="0.01"
                />
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Category</label>
                <select name="category" value={form.category} onChange={handleChange}>
                  {categories.map(c => (
                    <option key={c} value={c}>{c.replace('_', ' ')}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Date</label>
                <input
                  type="date"
                  name="transactionDate"
                  value={form.transactionDate}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div className="form-group">
              <label>Description (optional)</label>
              <input
                type="text"
                name="description"
                placeholder="e.g. Lunch at restaurant"
                value={form.description}
                onChange={handleChange}
              />
            </div>
            <button type="submit" className="submit-btn" disabled={submitting}>
              {submitting ? 'Adding...' : 'Add Transaction'}
            </button>
          </form>
        </div>
      )}

      {/* Transactions Table */}
      <div className="table-card">
        {loading ? (
          <div className="loading">Loading...</div>
        ) : transactions.length === 0 ? (
          <div className="no-data">No transactions this month. Add one above!</div>
        ) : (
          <table className="tx-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Type</th>
                <th>Category</th>
                <th>Description</th>
                <th>Amount</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map(tx => (
                <tr key={tx.id}>
                  <td>{tx.transactionDate}</td>
                  <td>
                    <span className={`badge ${tx.type === 'INCOME' ? 'income' : 'expense'}`}>
                      {tx.type}
                    </span>
                  </td>
                  <td>{tx.category?.replace('_', ' ')}</td>
                  <td>{tx.description || '-'}</td>
                  <td className={tx.type === 'INCOME' ? 'green' : 'red'}>
                    {tx.type === 'INCOME' ? '+' : '-'}₹{parseFloat(tx.amount).toLocaleString()}
                  </td>
                  <td>
                    <button className="delete-btn" onClick={() => handleDelete(tx.id)}>
                      🗑
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Transactions;