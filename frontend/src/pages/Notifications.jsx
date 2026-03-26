import React, { useEffect, useState } from 'react';
import API from '../api/axios';
import './Notifications.css';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchNotifications();
  }, []);

  const fetchNotifications = async () => {
    try {
      const res = await API.get('/api/notifications');
      setNotifications(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getTypeColor = (type) => {
    if (type === 'BUDGET_EXCEEDED') return '#e74c3c';
    if (type === 'BUDGET_ALERT') return '#f39c12';
    return '#3498db';
  };

  const getTypeEmoji = (type) => {
    if (type === 'BUDGET_EXCEEDED') return '🚨';
    if (type === 'BUDGET_ALERT') return '⚠️';
    return '📊';
  };

  return (
    <div className="notifications-page">
      <div className="page-header">
        <div>
          <h2>🔔 Notifications</h2>
          <p>Your budget alerts and activity history</p>
        </div>
      </div>

      {loading ? (
        <div className="loading">Loading notifications...</div>
      ) : notifications.length === 0 ? (
        <div className="no-data">
          <div style={{ fontSize: '3rem' }}>🔔</div>
          <p>No notifications yet.</p>
          <p style={{ fontSize: '0.85rem' }}>Alerts will appear here when your budget is exceeded.</p>
        </div>
      ) : (
        <div className="notifications-list">
          {notifications.map(n => (
            <div key={n.id} className={`notification-item ${n.status === 'FAILED' ? 'failed' : ''}`}>
              <div className="notif-icon" style={{ background: getTypeColor(n.type) + '20', color: getTypeColor(n.type) }}>
                {getTypeEmoji(n.type)}
              </div>
              <div className="notif-content">
                <div className="notif-subject">{n.subject}</div>
                <div className="notif-time">
                  {new Date(n.createdAt).toLocaleString('en-IN')}
                </div>
              </div>
              <div className="notif-meta">
                <span className={`status-badge ${n.status === 'SENT' ? 'sent' : 'failed'}`}>
                  {n.status}
                </span>
                <span className="type-badge" style={{ color: getTypeColor(n.type) }}>
                  {n.type?.replace('_', ' ')}
                </span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;