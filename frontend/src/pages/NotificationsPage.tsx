import React, { useState, useEffect } from 'react';
import { CheckCircle, XCircle, AlertCircle, Bell } from 'lucide-react';
import axios from 'axios';

interface Notification {
  id: string;
  type: string;
  subject: string;
  message: string;
  status: string;
  channel: string;
  createdAt: string;
  sentAt?: string;
  read: boolean;
}

export const NotificationsPage: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filter, setFilter] = useState<'all' | 'unread' | 'read'>('all');
  
  const token = localStorage.getItem('accessToken');
  const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000';

  // Fetch notifications
  const fetchNotifications = async () => {
    if (!token) {
      setError('Not authenticated');
      return;
    }

    try {
      setLoading(true);
      const response = await axios.get(
        `${API_URL}/api/v1/notifications/user?page=0&size=50`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      
      setNotifications(Array.isArray(response.data) ? response.data : []);
      setError(null);
    } catch (err) {
      setError('Failed to load notifications');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Mark as read
  const markAsRead = async (notificationId: string) => {
    try {
      await axios.put(
        `${API_URL}/api/v1/notifications/${notificationId}/read`,
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      
      setNotifications(
        notifications.map(n =>
          n.id === notificationId ? { ...n, read: true } : n
        )
      );
    } catch (err) {
      console.error('Failed to mark as read:', err);
    }
  };

  // Mark all as read
  const markAllAsRead = async () => {
    try {
      await Promise.all(
        notifications
          .filter(n => !n.read)
          .map(n =>
            axios.put(
              `${API_URL}/api/v1/notifications/${n.id}/read`,
              {},
              { headers: { Authorization: `Bearer ${token}` } }
            )
          )
      );
      
      setNotifications(notifications.map(n => ({ ...n, read: true })));
    } catch (err) {
      console.error('Failed to mark all as read:', err);
    }
  };

  // Refresh notifications
  const handleRefresh = () => {
    fetchNotifications();
  };

  // Fetch on mount
  useEffect(() => {
    fetchNotifications();
  }, [token]);

  // Filter notifications
  const filtered = notifications.filter(n => {
    if (filter === 'unread') return !n.read;
    if (filter === 'read') return n.read;
    return true;
  });

  const unreadCount = notifications.filter(n => !n.read).length;

  // Get notification icon
  const getIcon = (type: string) => {
    switch (type) {
      case 'ORDER_PLACED':
      case 'ORDER_CONFIRMED':
        return <CheckCircle className="text-green-500" size={20} />;
      case 'PAYMENT_FAILED':
      case 'ORDER_CANCELLED':
        return <XCircle className="text-red-500" size={20} />;
      default:
        return <AlertCircle className="text-blue-500" size={20} />;
    }
  };

  if (!token) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-16 text-center">
        <p className="text-gray-600">Please login to view notifications</p>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
            <Bell size={32} />
            Notifications
          </h1>
          
          <button
            onClick={handleRefresh}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Refresh
          </button>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="bg-gray-100 p-4 rounded-lg">
            <p className="text-gray-600 text-sm">Total</p>
            <p className="text-2xl font-bold text-gray-900">{notifications.length}</p>
          </div>
          
          <div className="bg-blue-100 p-4 rounded-lg">
            <p className="text-gray-600 text-sm">Unread</p>
            <p className="text-2xl font-bold text-blue-600">{unreadCount}</p>
          </div>
          
          <div className="bg-green-100 p-4 rounded-lg">
            <p className="text-gray-600 text-sm">Read</p>
            <p className="text-2xl font-bold text-green-600">
              {notifications.length - unreadCount}
            </p>
          </div>
        </div>

        {/* Filters and Actions */}
        <div className="flex justify-between items-center mb-6">
          <div className="flex gap-2">
            {(['all', 'unread', 'read'] as const).map(f => (
              <button
                key={f}
                onClick={() => setFilter(f)}
                className={`px-4 py-2 rounded-lg transition ${
                  filter === f
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-200 text-gray-900 hover:bg-gray-300'
                }`}
              >
                {f.charAt(0).toUpperCase() + f.slice(1)}
              </button>
            ))}
          </div>

          {unreadCount > 0 && (
            <button
              onClick={markAllAsRead}
              className="text-blue-600 hover:text-blue-700 font-medium text-sm"
            >
              Mark all as read
            </button>
          )}
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
          {error}
        </div>
      )}

      {/* Loading */}
      {loading && (
        <div className="text-center py-12">
          <p className="text-gray-600">Loading notifications...</p>
        </div>
      )}

      {/* Empty State */}
      {!loading && filtered.length === 0 && (
        <div className="text-center py-12">
          <Bell size={48} className="mx-auto text-gray-400 mb-4" />
          <p className="text-gray-600">
            {filter === 'all' && 'No notifications yet'}
            {filter === 'unread' && 'No unread notifications'}
            {filter === 'read' && 'No read notifications'}
          </p>
        </div>
      )}

      {/* Notifications List */}
      <div className="space-y-4">
        {filtered.map(notif => (
          <div
            key={notif.id}
            onClick={() => !notif.read && markAsRead(notif.id)}
            className={`p-6 rounded-lg border-2 cursor-pointer transition ${
              notif.read
                ? 'bg-gray-50 border-gray-200'
                : 'bg-blue-50 border-blue-200 hover:border-blue-400'
            }`}
          >
            <div className="flex gap-4">
              {/* Icon */}
              <div className="flex-shrink-0 mt-1">
                {getIcon(notif.type)}
              </div>

              {/* Content */}
              <div className="flex-grow">
                <div className="flex justify-between items-start mb-2">
                  <h3 className="font-semibold text-gray-900 text-lg">
                    {notif.subject}
                  </h3>
                  
                  <div className="flex gap-2 text-xs">
                    {!notif.read && (
                      <span className="bg-red-500 text-white px-2 py-1 rounded">
                        Unread
                      </span>
                    )}
                    <span className="bg-gray-200 text-gray-700 px-2 py-1 rounded">
                      {notif.type}
                    </span>
                  </div>
                </div>

                <p className="text-gray-700 mb-3">
                  {notif.message}
                </p>

                <div className="flex justify-between text-xs text-gray-500">
                  <span>
                    Channel: <strong>{notif.channel}</strong>
                  </span>
                  <span>
                    Status: <strong>{notif.status}</strong>
                  </span>
                  <span>
                    {new Date(notif.createdAt).toLocaleDateString()} at{' '}
                    {new Date(notif.createdAt).toLocaleTimeString()}
                  </span>
                </div>
              </div>

              {/* Mark as read button */}
              {!notif.read && (
                <button
                  onClick={() => markAsRead(notif.id)}
                  className="flex-shrink-0 px-3 py-1 bg-blue-600 text-white text-sm rounded hover:bg-blue-700 transition"
                >
                  Mark Read
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
