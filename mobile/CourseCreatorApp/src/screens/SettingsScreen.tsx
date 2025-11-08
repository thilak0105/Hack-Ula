/**
 * Settings Screen
 * App settings and configuration
 */

import React, {useState} from 'react';
import {View, Text, StyleSheet, ScrollView, Switch} from 'react-native';
import {Card, List, Divider} from 'react-native-paper';
import {RunAnywhereService} from '../services/runanywhere';

export default function SettingsScreen() {
  const [runanywhereEnabled, setRunanywhereEnabled] = useState(
    RunAnywhereService.isAvailable(),
  );

  return (
    <ScrollView style={styles.container}>
      <Card style={styles.card}>
        <Card.Content>
          <Text style={styles.sectionTitle}>AI Provider</Text>
          <List.Item
            title="RunAnywhere SDK"
            description={
              runanywhereEnabled
                ? 'On-device AI processing enabled'
                : 'Using backend API'
            }
            left={props => <List.Icon {...props} icon="brain" />}
            right={() => (
              <Switch
                value={runanywhereEnabled}
                onValueChange={setRunanywhereEnabled}
                disabled={!RunAnywhereService.isAvailable()}
              />
            )}
          />
        </Card.Content>
      </Card>

      <Card style={styles.card}>
        <Card.Content>
          <Text style={styles.sectionTitle}>About</Text>
          <List.Item
            title="Version"
            description="1.0.0"
            left={props => <List.Icon {...props} icon="information" />}
          />
          <Divider />
          <List.Item
            title="Course Creator"
            description="AI-Powered Learning Platform"
            left={props => <List.Icon {...props} icon="school" />}
          />
        </Card.Content>
      </Card>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  card: {
    margin: 16,
    elevation: 4,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 12,
    color: '#333',
  },
});


