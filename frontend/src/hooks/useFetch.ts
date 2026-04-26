import { useState, useCallback, useEffect } from 'react'

interface UseFetchOptions {
  immediate?: boolean
  deps?: unknown[]
}

export interface UseFetchReturn<T> {
  data: T | null
  loading: boolean
  error: string | null
  refetch: () => Promise<void>
}

/**
 * Generic custom hook for data fetching.
 * Handles loading, error states, and refetching.
 */
export function useFetch<T>(
  fetchFn: () => Promise<T>,
  options: UseFetchOptions = {}
): UseFetchReturn<T> {
  const { immediate = true, deps = [] } = options
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(immediate)
  const [error, setError] = useState<string | null>(null)

  const fetch = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const result = await fetchFn()
      setData(result)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An error occurred'
      setError(errorMessage)
      setData(null)
    } finally {
      setLoading(false)
    }
  }, [fetchFn])

  useEffect(() => {
    if (immediate) {
      fetch()
    }
  }, deps)

  return {
    data,
    loading,
    error,
    refetch: fetch,
  }
}

/**
 * Variant for paginated data fetching.
 */
export interface UseFetchPaginatedOptions extends UseFetchOptions {
  page?: number
  size?: number
}

export interface UseFetchPaginatedReturn<T> extends UseFetchReturn<T[]> {
  page: number
  size: number
  total: number
  setPage: (page: number) => void
  setSize: (size: number) => void
}

export function useFetchPaginated<T>(
  fetchFn: (page: number, size: number) => Promise<{ data: T[]; total: number }>,
  options: UseFetchPaginatedOptions = {}
): UseFetchPaginatedReturn<T> {
  const { immediate = true, page: initialPage = 0, size: initialSize = 10 } = options
  const [page, setPageState] = useState(initialPage)
  const [size, setSizeState] = useState(initialSize)
  const [data, setData] = useState<T[]>([])
  const [total, setTotal] = useState(0)
  const [loading, setLoading] = useState(immediate)
  const [error, setError] = useState<string | null>(null)

  const fetch = useCallback(async () => {
    try {
      setLoading(true)
      setError(null)
      const result = await fetchFn(page, size)
      setData(result.data)
      setTotal(result.total)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An error occurred'
      setError(errorMessage)
      setData([])
      setTotal(0)
    } finally {
      setLoading(false)
    }
  }, [fetchFn, page, size])

  useEffect(() => {
    if (immediate) {
      fetch()
    }
  }, [page, size, immediate])

  return {
    data,
    loading,
    error,
    page,
    size,
    total,
    setPage: setPageState,
    setSize: setSizeState,
    refetch: fetch,
  }
}
