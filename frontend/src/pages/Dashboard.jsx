import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import API from '../api/axios';
import './Dashboard.css';

const COLORS = ['#4ecca3', '#e74c3c', '#f39c12', '#3498db'];

const Dashboard = () => {
  const [summary, setSummary] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const name = localStorage.getItem('name');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [summaryRes, txRes, alertsRes] = await Promise.all([
        API.get('/api/transactions/summary'),
        API.get('/api/transactions'),
        API.get('/api/budgets/alerts'),
      ]);
      setSummary(summaryRes.data);
      setTransactions(txRes.data);
      setAlerts(alertsRes.data);
    } catch (err) {
      console.error('Failed to fetch dashboard data', err);
    } finally {
      setLoading(false);
    }
  };

  // Build bar chart data
  const barData = summary ? [
    {
      name: 'This Month',
      Income: summary.totalIncome,
      Expense: summary.totalExpense,
    }
  ] : [];

  // Build pie chart data from transactions by category
  const categoryMap = {};
  transactions.forEach(t => {
    if (t.type === 'EXPENSE') {
      categoryMap[t.category] = (categoryMap[t.category] || 0) + t.amount;
    }
  });
  const pieData = Object.entries(categoryMap).map(([name, value]) => ({ name, value }));

  if (loading) return <div className="loading">Loading dashboard...</div>;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h2>Welcome back, {name} 👋</h2>
        <p>Here's your financial overview for this month</p>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="card income">
          <div className="card-label">Total Income</div>
          <div className="card-amount">₹{summary?.totalIncome?.toLocaleString() || 0}</div>
        </div>
        <div className="card expense">
          <div className="card-label">Total Expense</div>
          <div className="card-amount">₹{summary?.totalExpense?.toLocaleString() || 0}</div>
        </div>
        <div className="card balance">
          <div className="card-label">Balance</div>
          <div className="card-amount">₹{summary?.balance?.toLocaleString() || 0}</div>
        </div>
        <div className="card alerts-card">
          <div className="card-label">Budget Alerts</div>
          <div className="card-amount">{alerts.length}</div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="charts-row">
        {/* Bar Chart */}
        <div className="chart-box">
          <h3>Income vs Expense</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={barData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip formatter={(val) => `₹${val.toLocaleString()}`} />
              <Legend />
              <Bar dataKey="Income" fill="#4ecca3" radius={[4,4,0,0]} />
              <Bar dataKey="Expense" fill="#e74c3c" radius={[4,4,0,0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Pie Chart */}
        <div className="chart-box">
          <h3>Expense by Category</h3>
          {pieData.length > 0 ? (
            <ResponsiveContainer width="100%" height={250}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  outerRadius={90}
                  dataKey="value"
                  label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                >
                  {pieData.map((entry, index) => (
                    <Cell key={index} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(val) => `₹${val.toLocaleString()}`} />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="no-data">No expense data yet</div>
          )}
        </div>
      </div>

      {/* Budget Alerts */}
      {alerts.length > 0 && (
        <div className="alerts-section">
          <h3>⚠️ Budget Alerts</h3>
          <div className="alerts-list">
            {alerts.map((alert, i) => (
              <div key={i} className={`alert-item ${alert.percentageUsed >= 100 ? 'exceeded' : 'warning'}`}>
                <span className="alert-category">{alert.category}</span>
                <span className="alert-message">{alert.alertMessage}</span>
                <span className="alert-percent">{alert.percentageUsed.toFixed(1)}%</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Recent Transactions */}
      <div className="recent-transactions">
        <h3>Recent Transactions</h3>
        {transactions.length === 0 ? (
          <div className="no-data">No transactions this month</div>
        ) : (
          <table className="tx-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Category</th>
                <th>Description</th>
                <th>Type</th>
                <th>Amount</th>
              </tr>
            </thead>
            <tbody>
              {transactions.slice(0, 5).map(tx => (
                <tr key={tx.id}>
                  <td>{tx.transactionDate}</td>
                  <td>{tx.category}</td>
                  <td>{tx.description || '-'}</td>
                  <td>
                    <span className={`badge ${tx.type === 'INCOME' ? 'income' : 'expense'}`}>
                      {tx.type}
                    </span>
                  </td>
                  <td className={tx.type === 'INCOME' ? 'green' : 'red'}>
                    {tx.type === 'INCOME' ? '+' : '-'}₹{tx.amount?.toLocaleString()}
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

export default Dashboard;