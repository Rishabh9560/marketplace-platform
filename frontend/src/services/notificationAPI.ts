import api from './authAPI'

export interface Notification {
  id: string
  userId: string
  type: string
  title: string
  message: string
  isRead: boolean
  actionUrl?: string
  metadata?: Record<string, any>
  createdAt: string
  readAt?: string
}

export interface NotificationPreferences {
  emailEnabled: boolean
  smsEnabled: boolean
  pushEnabled: boolean
  orderNotifications: boolean
  vendorNotifications: boolean
  promoNotifications: boolean
}

export const notificationAPI = {
  /**
   * Get user notifications
   */
  async getNotifications(page = 0, size = 20, unreadOnly = false): Promise<{
    notifications: Notification[]
    total: number
    unreadCount: number
  }> {
    let url = `/notifications?page=${page}&size=${size}`
    if (unreadOnly) url += '&unreadOnly=true'
    const response = await api.get(url)
    return response.data
  },

  /**
   * Mark notification as read
   */
  async markAsRead(notificationId: string): Promise<{ success: boolean }> {
    const response = await api.put(`/notifications/${notificationId}/read`)
    return response.data
  },

  /**
   * Mark all notifications as read
   */
  async markAllAsRead(): Promise<{ success: boolean; count: number }> {
    const response = await api.put('/notifications/mark-all-read')
    return response.data
  },

  /**
   * Delete notification
   */
  async deleteNotification(notificationId: string): Promise<{ success: boolean }> {
    const response = await api.delete(`/notifications/${notificationId}`)
    return response.data
  },

  /**
   * Delete all notifications
   */
  async deleteAllNotifications(): Promise<{ success: boolean; count: number }> {
    const response = await api.delete('/notifications')
    return response.data
  },

  /**
   * Get unread notification count
   */
  async getUnreadCount(): Promise<{ count: number }> {
    const response = await api.get('/notifications/unread-count')
    return response.data
  },

  /**
   * Get notification preferences
   */
  async getPreferences(): Promise<NotificationPreferences> {
    const response = await api.get<NotificationPreferences>('/notifications/preferences')
    return response.data
  },

  /**
   * Update notification preferences
   */
  async updatePreferences(preferences: Partial<NotificationPreferences>): Promise<NotificationPreferences> {
    const response = await api.put<NotificationPreferences>(
      '/notifications/preferences',
      preferences
    )
    return response.data
  },

  /**
   * Subscribe to push notifications
   */
  async subscribeToPush(subscription: PushSubscription): Promise<{ success: boolean }> {
    const response = await api.post('/notifications/subscribe-push', {
      subscription: subscription.toJSON()
    })
    return response.data
  },

  /**
   * Unsubscribe from push notifications
   */
  async unsubscribeFromPush(): Promise<{ success: boolean }> {
    const response = await api.post('/notifications/unsubscribe-push')
    return response.data
  },

  /**
   * Get notification categories with subscription status
   */
  async getSubscriptionStatus(): Promise<Array<{
    category: string
    email: boolean
    sms: boolean
    push: boolean
  }>> {
    const response = await api.get('/notifications/subscription-status')
    return response.data
  },

  /**
   * Subscribe to notification category
   */
  async subscribeToCategory(
    category: string,
    channels: string[]
  ): Promise<{ success: boolean }> {
    const response = await api.post(`/notifications/subscribe/${category}`, { channels })
    return response.data
  },

  /**
   * Unsubscribe from notification category
   */
  async unsubscribeFromCategory(
    category: string,
    channels: string[]
  ): Promise<{ success: boolean }> {
    const response = await api.post(`/notifications/unsubscribe/${category}`, { channels })
    return response.data
  }
}

export default notificationAPI
